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
class InquiryH5ReconcileOrderIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void h5ReconcileCreateListDetailAndQuickStatusShouldWork() throws Exception {
    MvcResult pickupListResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/h5/pickup-orders")
                    .param("contactMobile", "13800138000")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> pickupBody =
        objectMapper.readValue(pickupListResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> pickupData = (Map<?, ?>) pickupBody.get("data");
    java.util.List<?> pickupItems = (java.util.List<?>) pickupData.get("items");
    org.assertj.core.api.Assertions.assertThat(pickupItems).isNotEmpty();
    Map<?, ?> pickupFirst = (Map<?, ?>) pickupItems.get(0);
    String pickupOrderId = String.valueOf(pickupFirst.get("pickupId"));

    String createPayload =
        """
        {
          "pickupOrderId":"%s",
          "contactMobile":"13800138000",
          "statementMonth":"2026-04",
          "dueDate":"2026-05-20",
          "invoiceTitle":"唐山测试采购有限公司",
          "remark":"H09创建测试"
        }
        """
            .formatted(pickupOrderId);
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/reconcile-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("CREATED"));

    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/h5/reconcile-orders")
                    .param("contactMobile", "13800138000")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.items").isArray())
            .andReturn();
    Map<?, ?> body = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    java.util.List<?> items = (java.util.List<?>) data.get("items");
    org.assertj.core.api.Assertions.assertThat(items).isNotEmpty();
    Map<?, ?> first = (Map<?, ?>) items.get(0);
    String reconcileId = String.valueOf(first.get("reconcileId"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/h5/reconcile-orders/{reconcileOrderId}", reconcileId)
                .param("contactMobile", "13800138000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.reconcileId").value(reconcileId))
        .andExpect(jsonPath("$.data.order.goodsSummary").isNotEmpty());

    String quickStatusPayload =
        """
        {
          "contactMobile":"13800138000",
          "status":"PAID",
          "paidAmount":"1",
          "operator":"H5财务",
          "remark":"已完成回款"
        }
        """;
    mockMvc
        .perform(
            put("/api/v1/inquiries/h5/reconcile-orders/{reconcileOrderId}/quick-status", reconcileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(quickStatusPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("PAID"))
        .andExpect(jsonPath("$.data.message").value("对账单状态已更新"));
  }
}
