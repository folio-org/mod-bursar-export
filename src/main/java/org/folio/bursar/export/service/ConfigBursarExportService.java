package org.folio.bursar.export.service;

import org.folio.bursar.export.domain.dto.ScheduleConfig;
import org.folio.bursar.export.domain.dto.ScheduleConfigCollection;

public interface ConfigBursarExportService {

  void updateScheduleConfig(String configId, ScheduleConfig scheduleConfig);

  void postConfig(ScheduleConfig scheduleConfiguration);

  ScheduleConfigCollection getScheduleConfig();
}
