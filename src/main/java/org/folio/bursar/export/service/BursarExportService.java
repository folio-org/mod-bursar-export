package org.folio.bursar.export.service;

import java.util.List;
import org.folio.bursar.export.domain.dto.Account;

public interface BursarExportService {

  void exportFeesfinesToBursar();

  void transferAccounts(List<Account> accounts);

  List<Account> findAccounts();
}
