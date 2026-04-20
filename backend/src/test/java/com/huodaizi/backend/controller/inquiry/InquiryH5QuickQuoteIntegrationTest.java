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
class InquiryH5QuickQuoteIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void h5QuickQuoteInitAndSubmitShouldWork() throws Exception {
    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/h5/merchant/leads")
                    .param("merchantId", "S001")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> body = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    java.util.List<?> items = (java.util.List<?>) data.get("items");
    org.assertj.core.api.Assertions.assertThat(items).isNotEmpty();
    Map<?, ?> first = (Map<?, ?>) items.get(0);
    String leadId = String.valueOf(first.get("leadId"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/h5/quick-quote/init")
                .param("merchantId", "S001")
                .param("leadId", leadId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.merchantId").value("S001"))
        .andExpect(jsonPath("$.data.leadId").value(leadId))
        .andExpect(jsonPath("$.data.specText").isNotEmpty())
        .andExpect(jsonPath("$.data.defaultPaymentTerm").isNotEmpty());

    String quotePayload =
        """
        {
          "merchantId":"S001",
          "unitPrice":"3520",
          "deliveryDays":"2",
          "paymentTerm":"月结30天",
          "quoteRemark":"H07快捷报价测试"
        }
        """;
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/merchant/leads/" + leadId + "/quick-quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(quotePayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.leadId").value(leadId))
        .andExpect(jsonPath("$.data.status").value("QUOTED"))
        .andExpect(jsonPath("$.data.message").value("快捷报价已提交"));
  }
}
