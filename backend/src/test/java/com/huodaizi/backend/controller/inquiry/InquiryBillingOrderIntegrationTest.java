package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
class InquiryBillingOrderIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void listBillingOrdersShouldReturnRows() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/inquiries/merchant/billing/orders")
                    .param("merchantId", "S001")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.merchantId").value("S001"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(((List<?>) data.get("items"))).isNotEmpty();
  }

  @Test
  void billingDetailShouldWork() throws Exception {
    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/merchant/billing/orders")
                    .param("merchantId", "S001")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    Map<?, ?> first = (Map<?, ?>) ((List<?>) data.get("items")).get(0);
    String billId = String.valueOf(first.get("billId"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/merchant/billing/orders/{billingOrderId}", billId)
                .param("merchantId", "S001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.billId").value(billId));
  }

  @Test
  void payBillingOrderShouldUpdateStatus() throws Exception {
    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/merchant/billing/orders")
                    .param("merchantId", "S001")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    Map<?, ?> first = (Map<?, ?>) ((List<?>) data.get("items")).get(0);
    String billId = String.valueOf(first.get("billId"));

    String payload =
        """
        {
          "merchantId":"S001",
          "payAmount":"1000",
          "payChannel":"网银转账",
          "operator":"财务A",
          "remark":"首笔回款"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/inquiries/merchant/billing/orders/{billingOrderId}/pay", billId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.billId").value(billId))
        .andExpect(jsonPath("$.data.status").isNotEmpty())
        .andExpect(jsonPath("$.data.paidAmount").isNotEmpty());
  }
}
