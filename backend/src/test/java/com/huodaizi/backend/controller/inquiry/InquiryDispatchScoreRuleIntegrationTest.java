package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class InquiryDispatchScoreRuleIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void dispatchRuleShouldReturnPublicRule() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/inquiries/dispatch/score-rules")
                    .param("scene", "MERCHANT_LEAD"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.ruleName").value("线索分发评分规则"))
            .andExpect(jsonPath("$.data.ruleVersion").isNotEmpty())
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat((List<?>) data.get("dimensions")).isNotEmpty();
    assertThat((List<?>) data.get("bonusItems")).isNotEmpty();
    assertThat((List<?>) data.get("penaltyItems")).isNotEmpty();
  }

  @Test
  void dispatchRuleShouldDefaultToLeadDistribution() throws Exception {
    mockMvc
        .perform(get("/api/v1/inquiries/dispatch/score-rules"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ruleName").value("线索分发评分规则"));
  }
}
