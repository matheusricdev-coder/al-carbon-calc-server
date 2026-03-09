package br.com.actionlabs.carboncalc.rest;

import br.com.actionlabs.carboncalc.dto.CarbonCalculationResultDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;
import br.com.actionlabs.carboncalc.exception.CalculationNotFoundException;
import br.com.actionlabs.carboncalc.service.CarbonCalculationService;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoResponseDTO;
import br.com.actionlabs.carboncalc.exception.EmissionFactorNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OpenRestController.class)
class OpenRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CarbonCalculationService carbonCalculationService;

  // --- POST /open/start-calc ---

  @Test
  void startCalculation_withValidPayload_shouldReturn201WithId() throws Exception {
    StartCalcResponseDTO response = new StartCalcResponseDTO();
    response.setId("abc123");
    when(carbonCalculationService.startCalculation(any())).thenReturn(response);

    Map<String, String> body = Map.of(
        "name", "João",
        "email", "joao@email.com",
        "uf", "SP",
        "phoneNumber", "11999999999"
    );

    mockMvc.perform(post("/open/start-calc")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("abc123"));
  }

  @Test
  void startCalculation_missingRequiredField_shouldReturn400() throws Exception {
    Map<String, String> body = Map.of(
        "name", "João",
        "email", "joao@email.com"
        // uf and phoneNumber missing
    );

    mockMvc.perform(post("/open/start-calc")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.uf").exists())
        .andExpect(jsonPath("$.errors.phoneNumber").exists());
  }

  @Test
  void startCalculation_withInvalidEmail_shouldReturn400() throws Exception {
    Map<String, String> body = Map.of(
        "name", "João",
        "email", "not-an-email",
        "uf", "SP",
        "phoneNumber", "11999999999"
    );

    mockMvc.perform(post("/open/start-calc")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.email").exists());
  }

  @Test
  void startCalculation_withInvalidUf_shouldReturn400() throws Exception {
    Map<String, String> body = Map.of(
        "name", "João",
        "email", "joao@email.com",
        "uf", "INVALID",
        "phoneNumber", "11999999999"
    );

    mockMvc.perform(post("/open/start-calc")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.uf").exists());
  }

  // --- PUT /open/info ---

  @Test
  void updateInfo_withValidPayload_shouldReturn200WithSuccess() throws Exception {
    UpdateCalcInfoResponseDTO response = new UpdateCalcInfoResponseDTO();
    response.setSuccess(true);
    when(carbonCalculationService.updateCalcInfo(any())).thenReturn(response);

    Map<String, Object> body = Map.of(
        "id", "abc123",
        "energyConsumption", 200,
        "transportation", List.of(Map.of("type", "CAR", "monthlyDistance", 100)),
        "solidWasteTotal", 50,
        "recyclePercentage", 0.4
    );

    mockMvc.perform(put("/open/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  void updateInfo_missingId_shouldReturn400() throws Exception {
    Map<String, Object> body = Map.of(
        "energyConsumption", 200,
        "transportation", List.of(Map.of("type", "CAR", "monthlyDistance", 100)),
        "solidWasteTotal", 50,
        "recyclePercentage", 0.4
    );

    mockMvc.perform(put("/open/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.id").exists());
  }

  @Test
  void updateInfo_recyclePercentageOutOfRange_shouldReturn400() throws Exception {
    Map<String, Object> body = Map.of(
        "id", "abc123",
        "energyConsumption", 200,
        "transportation", List.of(Map.of("type", "CAR", "monthlyDistance", 100)),
        "solidWasteTotal", 50,
        "recyclePercentage", 1.5
    );

    mockMvc.perform(put("/open/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.recyclePercentage").exists());
  }

  @Test
  void updateInfo_withUnknownId_shouldReturn404() throws Exception {
    when(carbonCalculationService.updateCalcInfo(any()))
        .thenThrow(new CalculationNotFoundException("unknown"));

    Map<String, Object> body = Map.of(
        "id", "unknown",
        "energyConsumption", 200,
        "transportation", List.of(Map.of("type", "CAR", "monthlyDistance", 100)),
        "solidWasteTotal", 50,
        "recyclePercentage", 0.3
    );

    mockMvc.perform(put("/open/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").exists());
  }

  // --- GET /open/result/{id} ---

  @Test
  void getResult_withKnownId_shouldReturn200WithEmissions() throws Exception {
    CarbonCalculationResultDTO result = new CarbonCalculationResultDTO();
    result.setEnergy(100.0);
    result.setTransportation(19.0);
    result.setSolidWaste(35.0);
    result.setTotal(154.0);
    when(carbonCalculationService.getResult(eq("abc123"))).thenReturn(result);

    mockMvc.perform(get("/open/result/abc123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.energy").value(100.0))
        .andExpect(jsonPath("$.transportation").value(19.0))
        .andExpect(jsonPath("$.solidWaste").value(35.0))
        .andExpect(jsonPath("$.total").value(154.0));
  }

  @Test
  void getResult_withUnknownId_shouldReturn404() throws Exception {
    when(carbonCalculationService.getResult(eq("unknown")))
        .thenThrow(new CalculationNotFoundException("unknown"));

    mockMvc.perform(get("/open/result/unknown"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void getResult_withMissingEmissionFactor_shouldReturn422() throws Exception {
    when(carbonCalculationService.getResult(eq("abc123")))
        .thenThrow(new EmissionFactorNotFoundException("XX"));

    mockMvc.perform(get("/open/result/abc123"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").exists());
  }
}
