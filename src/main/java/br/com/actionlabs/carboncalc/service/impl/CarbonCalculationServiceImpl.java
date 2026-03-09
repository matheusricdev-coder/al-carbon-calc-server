package br.com.actionlabs.carboncalc.service.impl;

import br.com.actionlabs.carboncalc.dto.CarbonCalculationResultDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoResponseDTO;
import br.com.actionlabs.carboncalc.exception.CalculationNotFoundException;
import br.com.actionlabs.carboncalc.model.CarbonCalculation;
import br.com.actionlabs.carboncalc.model.SolidWasteEmissionFactor;
import br.com.actionlabs.carboncalc.model.Transportation;
import br.com.actionlabs.carboncalc.repository.CarbonCalculationRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
import br.com.actionlabs.carboncalc.service.CarbonCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarbonCalculationServiceImpl implements CarbonCalculationService {

  private final CarbonCalculationRepository calculationRepository;
  private final EnergyEmissionFactorRepository energyEmissionFactorRepository;
  private final TransportationEmissionFactorRepository transportationEmissionFactorRepository;
  private final SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;

  @Override
  public StartCalcResponseDTO startCalculation(StartCalcRequestDTO request) {
    CarbonCalculation calculation = new CarbonCalculation();
    calculation.setName(request.getName());
    calculation.setEmail(request.getEmail());
    calculation.setUf(request.getUf());
    calculation.setPhoneNumber(request.getPhoneNumber());

    CarbonCalculation saved = calculationRepository.save(calculation);

    StartCalcResponseDTO response = new StartCalcResponseDTO();
    response.setId(saved.getId());
    return response;
  }

  @Override
  public UpdateCalcInfoResponseDTO updateCalcInfo(UpdateCalcInfoRequestDTO request) {
    CarbonCalculation calculation = calculationRepository.findById(request.getId())
        .orElseThrow(() -> new CalculationNotFoundException(request.getId()));

    calculation.setEnergyConsumption(request.getEnergyConsumption());
    calculation.setSolidWasteTotal(request.getSolidWasteTotal());
    calculation.setRecyclePercentage(request.getRecyclePercentage());

    List<Transportation> transportationList = request.getTransportation().stream()
        .map(dto -> {
          Transportation t = new Transportation();
          t.setType(dto.getType());
          t.setMonthlyDistance(dto.getMonthlyDistance());
          return t;
        })
        .collect(Collectors.toList());
    calculation.setTransportation(transportationList);

    calculationRepository.save(calculation);

    UpdateCalcInfoResponseDTO response = new UpdateCalcInfoResponseDTO();
    response.setSuccess(true);
    return response;
  }

  @Override
  public CarbonCalculationResultDTO getResult(String id) {
    CarbonCalculation calculation = calculationRepository.findById(id)
        .orElseThrow(() -> new CalculationNotFoundException(id));

    double energyEmission = calculateEnergyEmission(calculation);
    double transportationEmission = calculateTransportationEmission(calculation);
    double solidWasteEmission = calculateSolidWasteEmission(calculation);

    CarbonCalculationResultDTO result = new CarbonCalculationResultDTO();
    result.setEnergy(energyEmission);
    result.setTransportation(transportationEmission);
    result.setSolidWaste(solidWasteEmission);
    result.setTotal(energyEmission + transportationEmission + solidWasteEmission);
    return result;
  }

  private double calculateEnergyEmission(CarbonCalculation calculation) {
    if (calculation.getEnergyConsumption() == null) return 0.0;
    return energyEmissionFactorRepository.findById(calculation.getUf())
        .map(factor -> calculation.getEnergyConsumption() * factor.getFactor())
        .orElse(0.0);
  }

  private double calculateTransportationEmission(CarbonCalculation calculation) {
    if (calculation.getTransportation() == null || calculation.getTransportation().isEmpty()) return 0.0;
    return calculation.getTransportation().stream()
        .mapToDouble(t -> transportationEmissionFactorRepository.findById(t.getType())
            .map(factor -> t.getMonthlyDistance() * factor.getFactor())
            .orElse(0.0))
        .sum();
  }

  private double calculateSolidWasteEmission(CarbonCalculation calculation) {
    if (calculation.getSolidWasteTotal() == null || calculation.getRecyclePercentage() == null) return 0.0;

    SolidWasteEmissionFactor factor = solidWasteEmissionFactorRepository.findById(calculation.getUf())
        .orElse(null);
    if (factor == null) return 0.0;

    double recyclableEmission = calculation.getSolidWasteTotal()
        * calculation.getRecyclePercentage()
        * factor.getRecyclableFactor();
    double nonRecyclableEmission = calculation.getSolidWasteTotal()
        * (1 - calculation.getRecyclePercentage())
        * factor.getNonRecyclableFactor();

    return recyclableEmission + nonRecyclableEmission;
  }
}
