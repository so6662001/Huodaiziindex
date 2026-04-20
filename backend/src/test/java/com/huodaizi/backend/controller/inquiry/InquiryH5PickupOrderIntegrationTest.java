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
class InquiryH5PickupOrderIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void h5PickupOrderCreateListDetailAndStatusShouldWork() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/inquiries/deal/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "inquiryId":"IQ20260418001",
                      "quoteId":"IQ20260418001-Q1",
                      "contactMobile":"13800138000",
                      "buyerCompany":"唐山测试采购有限公司",
                      "buyerContact":"李经理",
                      "buyerPhone":"13800138000"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    String createPayload =
        """
        {
          "inquiryId":"IQ20260418001",
          "quoteId":"IQ20260418001-Q1",
          "contactMobile":"13800138000",
          "buyerCompany":"唐山测试采购有限公司",
          "buyerContact":"李经理",
          "pickupSite":"唐山丰润货场B区",
          "pickupDate":"2026-04-21",
          "pickupVehicleNo":"冀B95270",
          "pickupDriverName":"刘师傅",
          "pickupDriverPhone":"13800003333",
          "agreedProtocol":true,
          "remark":"H08创建测试"
        }
        """;
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/pickup-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("CREATED"));

    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/h5/pickup-orders")
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
    String pickupId = String.valueOf(first.get("pickupId"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/h5/pickup-orders/{pickupOrderId}", pickupId)
                .param("contactMobile", "13800138000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.pickupId").value(pickupId))
        .andExpect(jsonPath("$.data.order.goodsSummary").isNotEmpty());

    String statusPayload =
        """
        {
          "contactMobile":"13800138000",
          "status":"IN_TRANSIT",
          "operator":"H5调度",
          "remark":"已安排发车"
        }
        """;
    mockMvc
        .perform(
            put("/api/v1/inquiries/h5/pickup-orders/{pickupOrderId}/quick-status", pickupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("IN_TRANSIT"))
        .andExpect(jsonPath("$.data.message").value("提货单状态已更新"));
  }
}
