package br.com.actionlabs.carboncalc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class StartCalcRequestDTO {
  @NotBlank
  private String name;
  @NotBlank
  @Email
  private String email;
  @NotBlank
  @Pattern(regexp = "[A-Za-z]{2}", message = "must be a valid 2-letter Brazilian state code")
  private String uf;
  @NotBlank
  private String phoneNumber;
}
