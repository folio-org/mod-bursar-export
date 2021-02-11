package org.folio.bursar.export.client;

import org.folio.bursar.export.domain.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "accounts-bulk")
public interface AccountBulkClient {

  @PostMapping(path = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
  String transferAccount(@RequestBody TransferRequest request);
}
