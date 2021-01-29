package org.folio.bursar.export.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users")
public interface UserClient {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  String getUserByQuery(@RequestParam("query") String query);
}
