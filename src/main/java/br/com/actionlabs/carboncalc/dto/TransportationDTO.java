package br.com.actionlabs.carboncalc.dto;

import br.com.actionlabs.carboncalc.enums.TransportationType;
import lombok.Data;

@Data
public class TransportationDTO {
  private TransportationType type;
  private int monthlyDistance;
}
