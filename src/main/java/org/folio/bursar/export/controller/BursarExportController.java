package org.folio.bursar.export.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.bursar.export.client.UserClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/bursars")
public class BursarExportController {

  private final UserClient client;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public String getUsers() {
    String query = "active==\"true\" and patronGroup==(\"503a81cd-6c26-400f-b620-14c08943697c\" or \"ad0bc554-d5bc-463c-85d1-5562127ae91b\")";
    return client.getUserByQuery(query);
  }

}
