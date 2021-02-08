package org.folio.bursar.export.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.bursar.export.domain.dto.ScheduleConfig;
import org.folio.bursar.export.domain.dto.ScheduleConfigCollection;
import org.folio.bursar.export.rest.resource.BursarExportApi;
import org.folio.bursar.export.service.ConfigBursarExportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/")
public class BursarExportController implements BursarExportApi {

  @Override
  public ResponseEntity<String> postBursarExport() {
    //stub
    return ResponseEntity.ok("Job launched");
  }

  private final ConfigBursarExportService configService;

  @Override
  public ResponseEntity<ScheduleConfigCollection> getBursarExportConfig() {
    var configs = configService.getScheduleConfig();
    return new ResponseEntity<>(configs, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<String> postBursarExportConfig(
    @Valid ScheduleConfig scheduleConfiguration) {
    configService.postConfig(scheduleConfiguration);

    return ResponseEntity.ok("Schedule configuration added");
  }

  @Override
  public ResponseEntity<String> putBursarExportConfig(
    String configId, @Valid ScheduleConfig scheduleConfig) {

    configService.updateScheduleConfig(configId, scheduleConfig);
    return ResponseEntity.ok("Schedule configuration updated");
  }
}
