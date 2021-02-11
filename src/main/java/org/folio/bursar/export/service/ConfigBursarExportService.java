package org.folio.bursar.export.service;

import java.util.Optional;
import org.folio.bursar.export.domain.dto.BursarExportConfig;
import org.folio.bursar.export.domain.dto.BursarExportConfigCollection;
import org.folio.bursar.export.domain.dto.ConfigModel;

public interface ConfigBursarExportService {

  void updateConfig(String configId, BursarExportConfig config);

  ConfigModel postConfig(BursarExportConfig bursarExportConfig);

  BursarExportConfigCollection getConfigCollection();

  Optional<BursarExportConfig> getConfig();
}
