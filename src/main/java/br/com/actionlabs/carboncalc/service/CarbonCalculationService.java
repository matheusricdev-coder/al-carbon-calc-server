package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.CarbonCalculationResultDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoResponseDTO;

public interface CarbonCalculationService {

  StartCalcResponseDTO startCalculation(StartCalcRequestDTO request);

  UpdateCalcInfoResponseDTO updateCalcInfo(UpdateCalcInfoRequestDTO request);

  CarbonCalculationResultDTO getResult(String id);
}
