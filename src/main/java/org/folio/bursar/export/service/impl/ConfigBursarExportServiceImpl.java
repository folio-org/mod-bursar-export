package org.folio.bursar.export.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.folio.bursar.export.client.ConfigurationClient;
import org.folio.bursar.export.domain.dto.ConfigModel;
import org.folio.bursar.export.domain.dto.ScheduleConfig;
import org.folio.bursar.export.domain.dto.ScheduleConfigCollection;
import org.folio.bursar.export.service.ConfigBursarExportService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Service
public class ConfigBursarExportServiceImpl implements ConfigBursarExportService {

  private static final String CONFIG_QUERY = "module==%s and configName==%s";
  private static final String MODULE_NAME = "mod-bursar-export";
  private static final String CONFIG_NAME = "schedule_parameters";
  private final ConfigurationClient client;
  private final ObjectMapper objectMapper;

  @Override
  public void updateScheduleConfig(String configId, ScheduleConfig scheduleConfig) {
    ConfigModel config = createConfigModel(scheduleConfig);
    client.putConfiguration(config, configId);
  }

  @Override
  public void postConfig(ScheduleConfig scheduleConfiguration) {
    ConfigModel config = createConfigModel(scheduleConfiguration);
    client.postConfiguration(config);
  }

  @SneakyThrows
  private ConfigModel createConfigModel(ScheduleConfig scheduleConfiguration) {
    var config = new ConfigModel();
    config.setModule(MODULE_NAME);
    config.setConfigName(CONFIG_NAME);
    config.setDescription("Parameters to schedule the job");
    config.setEnabled(true);
    config.setDefaultFlag(true);
    config.setValue(objectMapper.writeValueAsString(scheduleConfiguration));
    return config;
  }

  @SneakyThrows
  @Override
  public ScheduleConfigCollection getScheduleConfig() {
    final String configuration =
        client.getConfiguration(String.format(CONFIG_QUERY, MODULE_NAME, CONFIG_NAME));

    final JSONObject jsonObject = new JSONObject(configuration);
    if (jsonObject.getInt("totalRecords") == 0) {
      return emptyConfigCollection();
    }

    try {
      ScheduleConfig scheduleConfig = parseScheduleConfig(jsonObject);
      return createConfigCollection(scheduleConfig);
    } catch (JsonProcessingException e) {
      log.error(
          "Can not parse configuration for module {} with config name {}",
          MODULE_NAME,
          CONFIG_NAME);
      return emptyConfigCollection();
    }
  }

  private ScheduleConfig parseScheduleConfig(JSONObject jsonObject)
      throws com.fasterxml.jackson.core.JsonProcessingException {
    final JSONObject q = jsonObject.getJSONArray("configs").getJSONObject(0);
    final ConfigModel configModel = objectMapper.readValue(q.toString(), ConfigModel.class);
    final String value = configModel.getValue();
    var scheduleConfig = objectMapper.readValue(value, ScheduleConfig.class);
    scheduleConfig.setId(configModel.getId());
    return scheduleConfig;
  }

  private ScheduleConfigCollection createConfigCollection(ScheduleConfig scheduleConfig) {
    var configCollection = new ScheduleConfigCollection();
    configCollection.addConfigsItem(scheduleConfig);
    configCollection.setTotalRecords(1);
    return configCollection;
  }

  private ScheduleConfigCollection emptyConfigCollection() {
    var configCollection = new ScheduleConfigCollection();
    configCollection.setTotalRecords(0);
    return configCollection;
  }
}
