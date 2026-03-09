package br.com.actionlabs.carboncalc.dto;

import lombok.Data;

@Data
public class CarbonCalculationResultDTO {
  private double energy;
  private double transportation;
  private double solidWaste;
  private double total;
}
