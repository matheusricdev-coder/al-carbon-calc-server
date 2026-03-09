package br.com.actionlabs.carboncalc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCalcInfoRequestDTO {
  @NotBlank
  private String id;
  @NotNull
  @PositiveOrZero
  private Integer energyConsumption;
  @NotNull
  @Valid
  private List<TransportationDTO> transportation;
  @NotNull
  @PositiveOrZero
  private Integer solidWasteTotal;
  @NotNull
  @DecimalMin("0.0")
  @DecimalMax("1.0")
  private Double recyclePercentage;
}
