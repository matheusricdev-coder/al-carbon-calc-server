package br.com.actionlabs.carboncalc.dto;

import br.com.actionlabs.carboncalc.enums.TransportationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class TransportationDTO {
  @NotNull
  private TransportationType type;
  @PositiveOrZero
  private int monthlyDistance;
}
