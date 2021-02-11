package org.folio.bursar.export.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.folio.bursar.export.client.AccountBulkClient;
import org.folio.bursar.export.client.AccountClient;
import org.folio.bursar.export.client.UserClient;
import org.folio.bursar.export.domain.dto.Account;
import org.folio.bursar.export.domain.dto.AccountdataCollection;
import org.folio.bursar.export.domain.dto.BursarExportConfig;
import org.folio.bursar.export.domain.dto.User;
import org.folio.bursar.export.domain.dto.UserCollection;
import org.folio.bursar.export.service.BursarExportService;
import org.folio.bursar.export.service.ConfigBursarExportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = BursarExportServiceImpl.class)
class BursarExportServiceImplTest {

  @Autowired
  private BursarExportService service;
  @MockBean
  private UserClient userClient;
  @MockBean
  private AccountClient accountClient;
  @MockBean
  private ConfigBursarExportService configService;
  @MockBean
  private AccountBulkClient bulkClient;

  @Test
  @DisplayName("Config is not set")
  void noConfig() {
    Mockito.when(configService.getConfig()).thenReturn(Optional.empty());

    final List<Account> accounts = service.findAccounts();

    Assertions.assertTrue(accounts.isEmpty());
  }

  @Test
  @DisplayName("Config doesn't have required parameters")
  void configWithNullParams() {
    BursarExportConfig config = new BursarExportConfig();
    Mockito.when(configService.getConfig()).thenReturn(Optional.of(config));

    final List<Account> accounts = service.findAccounts();

    Assertions.assertTrue(accounts.isEmpty());
  }

  @Test
  @DisplayName("No users found for patron groups")
  void noUsers() {
    BursarExportConfig config = createBursarExportConfig();
    Mockito.when(configService.getConfig()).thenReturn(Optional.of(config));
    UserCollection users = createUserCollectionWithEmptyParams();
    Mockito.when(userClient.getUserByQuery(any())).thenReturn(users);

    var accounts = service.findAccounts();

    Assertions.assertTrue(accounts.isEmpty());
  }

  @Test
  @DisplayName("No accounts found for user id")
  void noAccounts() {
    BursarExportConfig config = createBursarExportConfig();
    Mockito.when(configService.getConfig()).thenReturn(Optional.of(config));
    UserCollection users = createSingleUserCollection();
    Mockito.when(userClient.getUserByQuery(any())).thenReturn(users);
    AccountdataCollection accountdataCollection = createAccountdataCollection();
    Mockito.when(accountClient.getAccounts(anyString(), anyLong()))
      .thenReturn(accountdataCollection);

    var accounts = service.findAccounts();

    Assertions.assertFalse(accounts.isEmpty());
  }

  @Test
  @DisplayName("Accounts are found and transferred")
  void transferAccounts() {
    BursarExportConfig config = createBursarExportConfig();
    Mockito.when(configService.getConfig()).thenReturn(Optional.of(config));
    UserCollection users = createSingleUserCollection();
    Mockito.when(userClient.getUserByQuery(any())).thenReturn(users);
    AccountdataCollection accountdataCollection = createAccountdataCollection();
    Mockito.when(accountClient.getAccounts(anyString(), anyLong()))
      .thenReturn(accountdataCollection);


    service.exportFeesfinesToBursar();

    verify(bulkClient, times(1)).transferAccount(any());
  }

  @Test
  @DisplayName("No accounts to transfer")
  void transferNoAccounts() {
    BursarExportConfig config = createBursarExportConfig();
    Mockito.when(configService.getConfig()).thenReturn(Optional.of(config));
    UserCollection users = createUserCollectionWithEmptyParams();
    Mockito.when(userClient.getUserByQuery(any())).thenReturn(users);

    service.exportFeesfinesToBursar();

    verify(bulkClient, times(0)).transferAccount(any());
  }

  private AccountdataCollection createAccountdataCollection() {
    AccountdataCollection accountdataCollection = new AccountdataCollection();
    accountdataCollection.setTotalRecords(1);
    Account account =
        new Account().remaining(BigDecimal.valueOf(100)).id(UUID.randomUUID().toString());
    accountdataCollection.setAccounts(List.of(account));
    return accountdataCollection;
  }

  private UserCollection createUserCollectionWithEmptyParams() {
    UserCollection users = new UserCollection();
    users.setTotalRecords(0);
    users.setUsers(Collections.emptyList());
    return users;
  }

  private UserCollection createSingleUserCollection() {
    UserCollection users = new UserCollection();
    users.setTotalRecords(1);
    User user = new User().id(UUID.randomUUID().toString());
    users.setUsers(List.of(user));
    return users;
  }

  private BursarExportConfig createBursarExportConfig() {
    BursarExportConfig config = new BursarExportConfig();
    config.setId(UUID.randomUUID().toString());
    config.setPatronGroups(List.of("some patron group"));
    config.setDaysOutstanding(9);
    return config;
  }
}
