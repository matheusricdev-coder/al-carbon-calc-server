package br.com.actionlabs.carboncalc.exception;

public class EmissionFactorNotFoundException extends RuntimeException {

  public EmissionFactorNotFoundException(String identifier) {
    super("Emission factor not found for: " + identifier);
  }
}
