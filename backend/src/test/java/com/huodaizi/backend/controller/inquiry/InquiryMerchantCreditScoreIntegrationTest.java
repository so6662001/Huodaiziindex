package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class InquiryMerchantCreditScoreIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void merchantCreditScoreShouldReturnOverview() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/inquiries/merchant/credit-score").param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.merchantId").value("S001"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(Integer.parseInt(String.valueOf(data.get("score")))).isGreaterThanOrEqualTo(0);
    assertThat(String.valueOf(data.get("grade"))).isNotBlank();
    assertThat(((java.util.List<?>) data.get("dimensions"))).isNotEmpty();
    assertThat(((java.util.List<?>) data.get("trend"))).hasSize(4);
  }

  @Test
  void merchantCreditScoreShouldRejectEmptyMerchantId() throws Exception {
    mockMvc
        .perform(get("/api/v1/inquiries/merchant/credit-score").param("merchantId", ""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
