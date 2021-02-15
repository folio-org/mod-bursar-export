package org.folio.bursar.export.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.folio.bursar.export.client.AccountBulkClient;
import org.folio.bursar.export.client.AccountClient;
import org.folio.bursar.export.client.ConfigurationClient;
import org.folio.bursar.export.client.UserClient;
import org.folio.bursar.export.service.BursarExportService;
import org.folio.bursar.export.service.ConfigBursarExportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({BursarExportController.class, ConfigBursarExportService.class})
class BursarExportControllerTest {
  public static final String CONFIG_RESPONSE =
      "    {\n"
          + "      \"configs\": [\n"
          + "        {\n"
          + "          \"id\": \"6c163f99-9df5-419d-9174-da638a1c76ed\",\n"
          + "          \"module\": \"mod-bursar-export\",\n"
          + "          \"configName\": \"query_parameters\",\n"
          + "          \"description\": \"for launch job\",\n"
          + "          \"default\": true,\n"
          + "          \"enabled\": true,\n"
          + "          \"value\": \"{\\\"id\\\":\\\"6c163f99-9df5-419d-9174-da638a1c76ed\\\",\\\"schedulePeriod\\\":\\\"DAY\\\",\\\"weekDays\\\":[\\\"FRIDAY\\\",\\\"MONDAY\\\"]}\",\n"
          + "          \"metadata\": {\n"
          + "            \"createdDate\": \"2021-02-04T05:25:19.580+00:00\",\n"
          + "            \"createdByUserId\": \"1d3b58cb-07b5-5fcd-8a2a-3ce06a0eb90f\",\n"
          + "            \"updatedDate\": \"2021-02-04T06:06:18.875+00:00\",\n"
          + "            \"updatedByUserId\": \"1d3b58cb-07b5-5fcd-8a2a-3ce06a0eb90f\"\n"
          + "          }\n"
          + "        }\n"
          + "      ],\n"
          + "      \"totalRecords\": 1,\n"
          + "      \"resultInfo\": {\n"
          + "        \"totalRecords\": 1,\n"
          + "        \"facets\": [],\n"
          + "        \"diagnostics\": []\n"
          + "      }\n"
          + "    }";
  private static final String CONFIG_CONTENT =
      "{\n"
          + "  \"id\": \"6c163f99-9df5-419d-9174-da638a1c76ed\",\n"
          + "  \"module\": \"mod-bursar-export\",\n"
          + "  \"configName\": \"bursar-test-config\",\n"
          + "  \"description\": \"for launch job\",\n"
          + "  \"default\": true,\n"
          + "  \"enabled\": true,\n"
          + "  \"value\": \"{\\\"id\\\":\\\"6c163f99-9df5-419d-9174-da638a1c76ed\\\",\\\"schedulePeriod\\\":\\\"DAY\\\",\\\"weekDays\\\":[\\\"FRIDAY\\\",\\\"MONDAY\\\"]}\"\n"
          + "}";

  @MockBean
  private ConfigurationClient configurationClient;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BursarExportService service;
  @MockBean
  private UserClient userClient;
  @MockBean
  private AccountClient accountClient;
  @MockBean
  private AccountBulkClient bulkClient;

  @Test
  @DisplayName("Run the batch job manually")
  void postBursarExport() throws Exception {
    mockMvc
        .perform(post("/bursar-export/").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(matchAll(status().isOk(), content().contentType("text/plain;charset=UTF-8")));
  }

  @Test
  @DisplayName("Set a new job configuration")
  void postBursarExportConfig() throws Exception {
    mockMvc
        .perform(
            post("/bursar-export/config")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(CONFIG_CONTENT))
        .andExpect(matchAll(status().isOk(), content().contentType("text/plain;charset=UTF-8")));
  }

  @Test
  @DisplayName("Update the job configuration")
  void updateJobConfig() throws Exception {
    mockMvc
        .perform(
            put("/bursar-export/config/6c163f99-9df5-419d-9174-da638a1c76ed")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(CONFIG_CONTENT))
        .andExpect(matchAll(status().isOk(), content().contentType("text/plain;charset=UTF-8")));
  }

  @Test
  @DisplayName("Get configs")
  void getConfigs() throws Exception {
    Mockito.when(configurationClient.getConfiguration(any())).thenReturn(CONFIG_RESPONSE);

    mockMvc
        .perform(get("/bursar-export/config").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(
            matchAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.totalRecords", is(1)),
                jsonPath("$.configs[0].id", is("6c163f99-9df5-419d-9174-da638a1c76ed")),
                jsonPath("$.configs[0].schedulePeriod", is("DAY")),
                jsonPath("$.configs[0].weekDays", hasItems("FRIDAY", "MONDAY"))));
  }
}
