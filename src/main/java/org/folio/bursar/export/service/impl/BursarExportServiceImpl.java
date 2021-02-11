package org.folio.bursar.export.service.impl;

import static java.util.stream.Collectors.joining;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.folio.bursar.export.client.AccountBulkClient;
import org.folio.bursar.export.client.AccountClient;
import org.folio.bursar.export.client.UserClient;
import org.folio.bursar.export.domain.dto.Account;
import org.folio.bursar.export.domain.dto.AccountdataCollection;
import org.folio.bursar.export.domain.dto.BursarExportConfig;
import org.folio.bursar.export.domain.dto.TransferRequest;
import org.folio.bursar.export.domain.dto.User;
import org.folio.bursar.export.domain.dto.UserCollection;
import org.folio.bursar.export.service.BursarExportService;
import org.folio.bursar.export.service.ConfigBursarExportService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class BursarExportServiceImpl implements BursarExportService {

  private static final String SERVICE_POINT_ONLINE = "7c5abc9f-f3d7-4856-b8d7-6712462ca007";
  private static final String PAYMENT_METHOD = "Bursar";
  private static final String USER_NAME = "Automatic process";
  private static final String ACCOUNT_QUERY =
      "userId==%s and remaining > 0.0 and metadata.createdDate>=%s";
  private static final String USER_QUERY = "(active==\"true\" and patronGroup==%s)";
  private static final long DEFAULT_LIMIT = 10000L;
  private final Collector<CharSequence, ?, String> toQueryParameters = joining(" or ", "(", ")");

  private final UserClient userClient;
  private final AccountClient accountClient;
  private final ConfigBursarExportService configService;
  private final AccountBulkClient bulkClient;

  @Override
  public void exportFeesfinesToBursar() {
    final List<Account> accounts = findAccounts();
    if (accounts.isEmpty()) {
      return;
    }
    transferAccounts(accounts);
  }

  @Override
  public void transferAccounts(List<Account> accounts) {
    TransferRequest request = toTransferRequest(accounts);
    bulkClient.transferAccount(request);
  }

  private TransferRequest toTransferRequest(List<Account> accounts) {
    BigDecimal remainingAmount = BigDecimal.ZERO;
    List<String> accountIds = new ArrayList<>();

    for (Account account : accounts) {
      remainingAmount = remainingAmount.add(account.getRemaining());
      accountIds.add(account.getId());
    }

    return new TransferRequest()
        .withAmount(remainingAmount.doubleValue())
        .withPaymentMethod(PAYMENT_METHOD)
        .withServicePointId(SERVICE_POINT_ONLINE)
        .withNotifyPatron(false)
        .withUserName(USER_NAME)
        .withAccountIds(accountIds);
  }

  @Override
  public List<Account> findAccounts() {
    final Optional<BursarExportConfig> configurations = configService.getConfig();
    if (configurations.isEmpty()) {
      log.info("There is no configuration to run the batch job");
      return Collections.emptyList();
    }

    final BursarExportConfig config = configurations.get();
    var daysToSubtract = config.getDaysOutstanding();
    var patronGroups = config.getPatronGroups();
    if (daysToSubtract == null || patronGroups == null) {
      log.info("Can not create query for batch job, cause config parameters are null");
      return Collections.emptyList();
    }

    final String groupIds = patronGroups.stream().collect(toQueryParameters);
    final LocalDate localDate = LocalDate.now().minusDays(daysToSubtract);
    var userResponse = userClient.getUserByQuery(String.format(USER_QUERY, groupIds));
    if (userResponse == null || userResponse.getTotalRecords() == 0) {
      log.info("There are no active users for patrons groups {}", groupIds);
      return Collections.emptyList();
    }

    final AccountdataCollection accounts = findAccounts(localDate, userResponse);
    return accounts.getAccounts();
  }

  private AccountdataCollection findAccounts(LocalDate localDate, UserCollection userResponse) {
    final List<User> users = userResponse.getUsers();
    final String userIdsAsParameter = users.stream().map(User::getId).collect(toQueryParameters);
    final String accountQuery = String.format(ACCOUNT_QUERY, userIdsAsParameter, localDate);
    return accountClient.getAccounts(accountQuery, DEFAULT_LIMIT);
  }
}
