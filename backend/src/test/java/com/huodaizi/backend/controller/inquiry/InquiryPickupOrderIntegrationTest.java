package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
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
class InquiryPickupOrderIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void createPickupOrderShouldWorkAfterDealConfirmed() throws Exception {
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

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/inquiries/pickup-orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "inquiryId":"IQ20260418001",
                          "quoteId":"IQ20260418001-Q1",
                          "contactMobile":"13800138000",
                          "buyerCompany":"唐山测试采购有限公司",
                          "buyerContact":"李经理",
                          "pickupSite":"唐山丰润货场A区",
                          "pickupDate":"2026-04-20",
                          "pickupVehicleNo":"冀B12345",
                          "pickupDriverName":"王师傅",
                          "pickupDriverPhone":"13800001111",
                          "agreedProtocol":true,
                          "remark":"请提前备货"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.pickupId").isNotEmpty())
            .andExpect(jsonPath("$.data.status").value("CREATED"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(String.valueOf(data.get("pickupNo"))).startsWith("PU-");
  }

  @Test
  void listAndDetailShouldFilterByBuyerPhone() throws Exception {
    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/pickup-orders")
                    .param("contactMobile", "13800138000")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> listBody = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> listData = (Map<?, ?>) listBody.get("data");
    assertThat(((Number) listData.get("total")).intValue()).isGreaterThanOrEqualTo(1);

    Object firstObj = ((java.util.List<?>) listData.get("items")).get(0);
    Map<?, ?> first = (Map<?, ?>) firstObj;
    String pickupId = String.valueOf(first.get("pickupId"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/pickup-orders/{pickupOrderId}", pickupId)
                .param("contactMobile", "13800138000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.pickupId").value(pickupId));
  }

  @Test
  void updateStatusShouldRequireMatchedPhone() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/inquiries/deal/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "inquiryId":"IQ20260418001",
                      "quoteId":"IQ20260418001-Q2",
                      "contactMobile":"13800138000",
                      "buyerCompany":"唐山测试采购有限公司",
                      "buyerContact":"李经理",
                      "buyerPhone":"13800138000"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/inquiries/pickup-orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "inquiryId":"IQ20260418001",
                          "quoteId":"IQ20260418001-Q2",
                          "contactMobile":"13800138000",
                          "pickupSite":"唐山丰润货场A区",
                          "pickupDate":"2026-04-20",
                          "pickupVehicleNo":"冀B88888",
                          "pickupDriverName":"张师傅",
                          "pickupDriverPhone":"13800002222",
                          "agreedProtocol":true
                        }
                        """))
            .andExpect(status().isOk())
            .andReturn();

    Map<?, ?> createBody =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
    String pickupId = String.valueOf(((Map<?, ?>) createBody.get("data")).get("pickupId"));

    mockMvc
        .perform(
            put("/api/v1/inquiries/pickup-orders/{pickupOrderId}/status", pickupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "contactMobile":"13800130000",
                      "status":"CONFIRMED",
                      "operator":"提货调度"
                    }
                    """))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("NOT_FOUND"));

    mockMvc
        .perform(
            put("/api/v1/inquiries/pickup-orders/{pickupOrderId}/status", pickupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "contactMobile":"13800138000",
                      "status":"IN_TRANSIT",
                      "operator":"提货调度",
                      "remark":"车辆已出发"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.status").value("IN_TRANSIT"));
  }
}
