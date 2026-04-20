package com.huodaizi.backend.controller.inquiry;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class InquiryH5MemberIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void h5MemberFlowShouldWork() throws Exception {
    mockMvc
        .perform(get("/api/v1/inquiries/h5/member/overview").param("merchantId", "S001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.merchantId").value("S001"));

    mockMvc
        .perform(get("/api/v1/inquiries/h5/member/plans").param("merchantId", "S001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.merchantId").value("S001"))
        .andExpect(jsonPath("$.data.plans").isArray());

    String createPayload =
        """
        {
          "merchantId":"S001",
          "planCode":"PLAN_PRO",
          "billingCycle":"MONTHLY",
          "operator":"H5运营"
        }
        """;
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/member/open")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.merchantId").value("S001"))
        .andExpect(jsonPath("$.data.status").value("ACTIVE"));

    MvcResult mineResult =
        mockMvc
            .perform(get("/api/v1/inquiries/h5/member/mine").param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.merchantId").value("S001"))
            .andExpect(jsonPath("$.data.items").isArray())
            .andReturn();
    Map<?, ?> body = objectMapper.readValue(mineResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    java.util.List<?> items = (java.util.List<?>) data.get("items");
    org.assertj.core.api.Assertions.assertThat(items).isNotEmpty();
  }
}
