package org.folio.bursar.export.client;

import org.folio.bursar.export.domain.dto.AccountdataCollection;
import org.folio.bursar.export.domain.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "accounts")
public interface AccountClient {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  AccountdataCollection getAccounts(@RequestParam String query, @RequestParam long limit);

  @PostMapping(path = "/{accountId}/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
  String transferAccount(@PathVariable String accountId, @RequestBody TransferRequest request);
}
