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
class InquiryDealConfirmIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void dealPreviewShouldReturnQuoteSummary() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/inquiries/deal/preview")
                    .param("inquiryId", "IQ20260418001")
                    .param("quoteId", "IQ20260418001-Q1")
                    .param("contactMobile", "13800138000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(String.valueOf(data.get("inquiryId"))).isEqualTo("IQ20260418001");
    assertThat(String.valueOf(data.get("quoteId"))).isEqualTo("IQ20260418001-Q1");
    assertThat(String.valueOf(data.get("supplierName"))).isNotBlank();
  }

  @Test
  void dealConfirmShouldSetInquiryToDealDone() throws Exception {
    String payload =
        """
        {
          "inquiryId":"IQ20260418001",
          "quoteId":"IQ20260418001-Q1",
          "contactMobile":"13800138000",
          "buyerCompany":"唐山测试采购有限公司",
          "buyerContact":"李经理",
          "buyerPhone":"13800138000",
          "expectedSignDate":"2026-05-01",
          "remark":"按标准合同执行"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/inquiries/deal/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.dealStatus").value("DEAL_DONE"));

    mockMvc
        .perform(get("/api/v1/inquiries").param("contactMobile", "13800138000").param("page", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.dealDoneCount").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
  }

  @Test
  void dealConfirmShouldRejectWrongMobile() throws Exception {
    String payload =
        """
        {
          "inquiryId":"IQ20260418001",
          "quoteId":"IQ20260418001-Q1",
          "contactMobile":"13800130000",
          "buyerCompany":"唐山测试采购有限公司",
          "buyerContact":"李经理",
          "buyerPhone":"13800138000"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/inquiries/deal/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("NOT_FOUND"));
  }

  @Test
  void pickupOrderFlowShouldWork() throws Exception {
    String createPayload =
        """
        {
          "inquiryId":"IQ20260418001",
          "quoteId":"IQ20260418001-Q1",
          "contactMobile":"13800138000",
          "buyerCompany":"唐山测试采购有限公司",
          "buyerContact":"李经理",
          "pickupSite":"唐山丰润提货点",
          "pickupDate":"2026-05-02",
          "pickupVehicleNo":"冀B12345",
          "pickupDriverName":"张司机",
          "pickupDriverPhone":"13800138001",
          "agreedProtocol":true,
          "remark":"先发提货码"
        }
        """;

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/inquiries/pickup-orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.pickupId").isNotEmpty())
            .andExpect(jsonPath("$.data.status").value("CREATED"))
            .andReturn();

    Map<?, ?> createBody = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> createData = (Map<?, ?>) createBody.get("data");
    String pickupId = String.valueOf(createData.get("pickupId"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/pickup-orders")
                .param("contactMobile", "13800138000")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));

    mockMvc
        .perform(
            get("/api/v1/inquiries/pickup-orders/{pickupOrderId}", pickupId)
                .param("contactMobile", "13800138000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.pickupId").value(pickupId));

    String updatePayload =
        """
        {
          "contactMobile":"13800138000",
          "status":"CONFIRMED",
          "operator":"赵运营",
          "remark":"卖方已确认放货"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/inquiries/pickup-orders/{pickupOrderId}/status", pickupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.status").value("CONFIRMED"))
        .andExpect(jsonPath("$.data.order.statusText").value("已确认"));
  }
}
