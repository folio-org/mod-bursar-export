package org.folio.bursar.export.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;

@With
@AllArgsConstructor
@RequiredArgsConstructor
public class TransferRequest {
  private List<String> accountIds;
  private double amount;
  private String servicePointId;
  private String userName;
  private String paymentMethod;
  private boolean notifyPatron;
}
