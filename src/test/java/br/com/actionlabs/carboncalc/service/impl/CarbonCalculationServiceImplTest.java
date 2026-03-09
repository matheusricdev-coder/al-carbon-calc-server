package br.com.actionlabs.carboncalc.service.impl;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import br.com.actionlabs.carboncalc.exception.CalculationNotFoundException;
import br.com.actionlabs.carboncalc.exception.EmissionFactorNotFoundException;
import br.com.actionlabs.carboncalc.model.*;
import br.com.actionlabs.carboncalc.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarbonCalculationServiceImplTest {

  @Mock private CarbonCalculationRepository calculationRepository;
  @Mock private EnergyEmissionFactorRepository energyEmissionFactorRepository;
  @Mock private TransportationEmissionFactorRepository transportationEmissionFactorRepository;
  @Mock private SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;

  @InjectMocks private CarbonCalculationServiceImpl service;

  @Test
  void startCalculation_shouldPersistUserDataAndReturnGeneratedId() {
    StartCalcRequestDTO request = new StartCalcRequestDTO();
    request.setName("João");
    request.setEmail("joao@email.com");
    request.setUf("SP");
    request.setPhoneNumber("11999999999");

    CarbonCalculation saved = new CarbonCalculation();
    saved.setId("calc-id-123");
    when(calculationRepository.save(any())).thenReturn(saved);

    StartCalcResponseDTO response = service.startCalculation(request);

    assertThat(response.getId()).isEqualTo("calc-id-123");

    ArgumentCaptor<CarbonCalculation> captor = ArgumentCaptor.forClass(CarbonCalculation.class);
    verify(calculationRepository).save(captor.capture());
    CarbonCalculation persisted = captor.getValue();
    assertThat(persisted.getName()).isEqualTo("João");
    assertThat(persisted.getEmail()).isEqualTo("joao@email.com");
    assertThat(persisted.getUf()).isEqualTo("SP");
    assertThat(persisted.getPhoneNumber()).isEqualTo("11999999999");
  }

  @Test
  void updateCalcInfo_shouldOverwriteAllFieldsAndReturnSuccess() {
    CarbonCalculation existing = new CarbonCalculation();
    existing.setId("calc-id-123");
    existing.setUf("SP");
    when(calculationRepository.findById("calc-id-123")).thenReturn(Optional.of(existing));
    when(calculationRepository.save(any())).thenReturn(existing);

    TransportationDTO transportDto = new TransportationDTO();
    transportDto.setType(TransportationType.CAR);
    transportDto.setMonthlyDistance(100);

    UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
    request.setId("calc-id-123");
    request.setEnergyConsumption(200);
    request.setTransportation(List.of(transportDto));
    request.setSolidWasteTotal(50);
    request.setRecyclePercentage(0.3);

    UpdateCalcInfoResponseDTO response = service.updateCalcInfo(request);

    assertThat(response.isSuccess()).isTrue();
    verify(calculationRepository).save(existing);
    assertThat(existing.getEnergyConsumption()).isEqualTo(200);
    assertThat(existing.getSolidWasteTotal()).isEqualTo(50);
    assertThat(existing.getRecyclePercentage()).isEqualTo(0.3);
    assertThat(existing.getTransportation()).hasSize(1);
    assertThat(existing.getTransportation().get(0).getType()).isEqualTo(TransportationType.CAR);
    assertThat(existing.getTransportation().get(0).getMonthlyDistance()).isEqualTo(100);
  }

  @Test
  void updateCalcInfo_withUnknownId_shouldThrowCalculationNotFoundException() {
    when(calculationRepository.findById("unknown")).thenReturn(Optional.empty());

    UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
    request.setId("unknown");

    assertThatThrownBy(() -> service.updateCalcInfo(request))
        .isInstanceOf(CalculationNotFoundException.class)
        .hasMessageContaining("unknown");
  }

  @Test
  void getResult_shouldCalculateCarbonEmissionsCorrectly() {
    Transportation transport = new Transportation();
    transport.setType(TransportationType.CAR);
    transport.setMonthlyDistance(100);

    CarbonCalculation calc = new CarbonCalculation();
    calc.setId("calc-id-123");
    calc.setUf("SP");
    calc.setEnergyConsumption(200);
    calc.setTransportation(List.of(transport));
    calc.setSolidWasteTotal(50);
    calc.setRecyclePercentage(0.4);

    EnergyEmissionFactor energyFactor = new EnergyEmissionFactor();
    energyFactor.setUf("SP");
    energyFactor.setFactor(0.5);

    TransportationEmissionFactor carFactor = new TransportationEmissionFactor();
    carFactor.setFactor(0.19);

    SolidWasteEmissionFactor wasteFactor = new SolidWasteEmissionFactor();
    wasteFactor.setUf("SP");
    wasteFactor.setRecyclableFactor(0.4);
    wasteFactor.setNonRecyclableFactor(0.9);

    when(calculationRepository.findById("calc-id-123")).thenReturn(Optional.of(calc));
    when(energyEmissionFactorRepository.findById("SP")).thenReturn(Optional.of(energyFactor));
    when(transportationEmissionFactorRepository.findById(TransportationType.CAR)).thenReturn(Optional.of(carFactor));
    when(solidWasteEmissionFactorRepository.findById("SP")).thenReturn(Optional.of(wasteFactor));

    CarbonCalculationResultDTO result = service.getResult("calc-id-123");

    // energy: 200 * 0.5 = 100.0
    assertThat(result.getEnergy()).isEqualTo(100.0);
    // transportation: 100 * 0.19 = 19.0
    assertThat(result.getTransportation()).isEqualTo(19.0);
    // solidWaste: 50 * 0.4 * 0.4 + 50 * 0.6 * 0.9 = 8.0 + 27.0 = 35.0
    assertThat(result.getSolidWaste()).isEqualTo(35.0);
    // total: 100 + 19 + 35 = 154.0
    assertThat(result.getTotal()).isEqualTo(154.0);
  }

  @Test
  void getResult_withUnknownId_shouldThrowCalculationNotFoundException() {
    when(calculationRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getResult("unknown"))
        .isInstanceOf(CalculationNotFoundException.class)
        .hasMessageContaining("unknown");
  }

  @Test
  void getResult_withMissingEnergyFactor_shouldThrowEmissionFactorNotFoundException() {
    CarbonCalculation calc = new CarbonCalculation();
    calc.setId("calc-id-123");
    calc.setUf("XX");
    calc.setEnergyConsumption(200);
    calc.setTransportation(List.of());
    calc.setSolidWasteTotal(0);
    calc.setRecyclePercentage(0.0);

    when(calculationRepository.findById("calc-id-123")).thenReturn(Optional.of(calc));
    when(energyEmissionFactorRepository.findById("XX")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getResult("calc-id-123"))
        .isInstanceOf(EmissionFactorNotFoundException.class)
        .hasMessageContaining("XX");
  }

  @Test
  void getResult_withRecyclePercentageAtZero_shouldCountAllAsNonRecyclable() {
    CarbonCalculation calc = new CarbonCalculation();
    calc.setId("calc-id-123");
    calc.setUf("SP");
    calc.setEnergyConsumption(0);
    calc.setTransportation(List.of());
    calc.setSolidWasteTotal(100);
    calc.setRecyclePercentage(0.0);

    EnergyEmissionFactor energyFactor = new EnergyEmissionFactor();
    energyFactor.setFactor(0.5);
    SolidWasteEmissionFactor wasteFactor = new SolidWasteEmissionFactor();
    wasteFactor.setRecyclableFactor(0.4);
    wasteFactor.setNonRecyclableFactor(0.9);

    when(calculationRepository.findById("calc-id-123")).thenReturn(Optional.of(calc));
    when(energyEmissionFactorRepository.findById("SP")).thenReturn(Optional.of(energyFactor));
    when(solidWasteEmissionFactorRepository.findById("SP")).thenReturn(Optional.of(wasteFactor));

    CarbonCalculationResultDTO result = service.getResult("calc-id-123");

    // solidWaste = 100 * 0.0 * 0.4 + 100 * 1.0 * 0.9 = 0 + 90 = 90
    assertThat(result.getSolidWaste()).isEqualTo(90.0);
    assertThat(result.getTotal()).isEqualTo(90.0);
  }

  @Test
  void getResult_withRecyclePercentageAtOne_shouldCountAllAsRecyclable() {
    CarbonCalculation calc = new CarbonCalculation();
    calc.setId("calc-id-123");
    calc.setUf("SP");
    calc.setEnergyConsumption(0);
    calc.setTransportation(List.of());
    calc.setSolidWasteTotal(100);
    calc.setRecyclePercentage(1.0);

    EnergyEmissionFactor energyFactor = new EnergyEmissionFactor();
    energyFactor.setFactor(0.5);
    SolidWasteEmissionFactor wasteFactor = new SolidWasteEmissionFactor();
    wasteFactor.setRecyclableFactor(0.4);
    wasteFactor.setNonRecyclableFactor(0.9);

    when(calculationRepository.findById("calc-id-123")).thenReturn(Optional.of(calc));
    when(energyEmissionFactorRepository.findById("SP")).thenReturn(Optional.of(energyFactor));
    when(solidWasteEmissionFactorRepository.findById("SP")).thenReturn(Optional.of(wasteFactor));

    CarbonCalculationResultDTO result = service.getResult("calc-id-123");

    // solidWaste = 100 * 1.0 * 0.4 + 100 * 0.0 * 0.9 = 40 + 0 = 40
    assertThat(result.getSolidWaste()).isEqualTo(40.0);
    assertThat(result.getTotal()).isEqualTo(40.0);
  }

  @Test
  void getResult_withEmptyTransportation_shouldReturnZeroTransportationEmission() {
    CarbonCalculation calc = new CarbonCalculation();
    calc.setId("calc-id-123");
    calc.setUf("SP");
    calc.setEnergyConsumption(0);
    calc.setTransportation(List.of());
    calc.setSolidWasteTotal(0);
    calc.setRecyclePercentage(0.0);

    EnergyEmissionFactor energyFactor = new EnergyEmissionFactor();
    energyFactor.setFactor(0.5);
    SolidWasteEmissionFactor wasteFactor = new SolidWasteEmissionFactor();
    wasteFactor.setRecyclableFactor(0.4);
    wasteFactor.setNonRecyclableFactor(0.9);

    when(calculationRepository.findById("calc-id-123")).thenReturn(Optional.of(calc));
    when(energyEmissionFactorRepository.findById("SP")).thenReturn(Optional.of(energyFactor));
    when(solidWasteEmissionFactorRepository.findById("SP")).thenReturn(Optional.of(wasteFactor));

    CarbonCalculationResultDTO result = service.getResult("calc-id-123");

    assertThat(result.getTransportation()).isEqualTo(0.0);
    assertThat(result.getTotal()).isEqualTo(0.0);
  }

  @Test
  void updateCalcInfo_calledTwice_shouldCompletelyOverwritePreviousData() {
    CarbonCalculation existing = new CarbonCalculation();
    existing.setId("calc-id-123");
    existing.setUf("SP");
    existing.setEnergyConsumption(999);
    existing.setSolidWasteTotal(999);
    existing.setRecyclePercentage(0.9);

    TransportationDTO bikeDto = new TransportationDTO();
    bikeDto.setType(TransportationType.BICYCLE);
    bikeDto.setMonthlyDistance(50);

    when(calculationRepository.findById("calc-id-123")).thenReturn(Optional.of(existing));
    when(calculationRepository.save(any())).thenReturn(existing);

    UpdateCalcInfoRequestDTO secondRequest = new UpdateCalcInfoRequestDTO();
    secondRequest.setId("calc-id-123");
    secondRequest.setEnergyConsumption(10);
    secondRequest.setTransportation(List.of(bikeDto));
    secondRequest.setSolidWasteTotal(5);
    secondRequest.setRecyclePercentage(0.1);

    service.updateCalcInfo(secondRequest);

    assertThat(existing.getEnergyConsumption()).isEqualTo(10);
    assertThat(existing.getSolidWasteTotal()).isEqualTo(5);
    assertThat(existing.getRecyclePercentage()).isEqualTo(0.1);
    assertThat(existing.getTransportation()).hasSize(1);
    assertThat(existing.getTransportation().get(0).getType()).isEqualTo(TransportationType.BICYCLE);
  }
}
