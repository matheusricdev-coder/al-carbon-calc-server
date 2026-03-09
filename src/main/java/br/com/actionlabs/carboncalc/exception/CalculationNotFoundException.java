package br.com.actionlabs.carboncalc.exception;

public class CalculationNotFoundException extends RuntimeException {

  public CalculationNotFoundException(String id) {
    super("Calculation not found with id: " + id);
  }
}
