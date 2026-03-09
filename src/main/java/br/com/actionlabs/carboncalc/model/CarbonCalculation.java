package br.com.actionlabs.carboncalc.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("carbonCalculation")
public class CarbonCalculation {

  @Id
  private String id;

  private String name;
  private String email;
  private String uf;
  private String phoneNumber;

  private Integer energyConsumption;
  private List<Transportation> transportation;
  private Integer solidWasteTotal;
  private Double recyclePercentage;
}
