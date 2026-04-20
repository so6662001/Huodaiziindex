package com.huodaizi.backend.controller.inquiry;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class InquiryH5MerchantLeadIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void h5MerchantLeadListShouldWork() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/inquiries/h5/merchant/leads")
                .param("merchantId", "S001")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.merchantId").value("S001"))
        .andExpect(jsonPath("$.data.items").isArray());
  }

  @Test
  void h5MerchantLeadQuickQuoteAndStatusShouldWork() throws Exception {
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

    String quotePayload =
        """
        {
          "merchantId":"S001",
          "unitPrice":"3510",
          "deliveryDays":"1",
          "paymentTerm":"月结15天",
          "quoteRemark":"H5快捷报价测试"
        }
        """;
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/merchant/leads/" + leadId + "/quick-quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(quotePayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("QUOTED"));

    String statusPayload =
        """
        {
          "merchantId":"S001",
          "status":"CONTACTED",
          "comment":"H5快捷状态更新"
        }
        """;
    mockMvc
        .perform(
            put("/api/v1/inquiries/h5/merchant/leads/" + leadId + "/quick-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("CONTACTED"));
  }
}
