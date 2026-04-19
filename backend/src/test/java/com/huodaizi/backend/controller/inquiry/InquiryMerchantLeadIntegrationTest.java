package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class InquiryMerchantLeadIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void merchantLeadListShouldWorkWithPaging() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/inquiries/merchant/leads")
                    .param("merchantId", "S001")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    List<?> items = (List<?>) data.get("items");
    assertThat(items).isNotEmpty();
    assertThat(((Number) data.get("total")).intValue()).isGreaterThan(0);
  }

  @Test
  void merchantLeadDetailShouldWork() throws Exception {
    String leadId = "ML-IQ20260418001-S001";
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/inquiries/merchant/leads/" + leadId).param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    Map<?, ?> lead = (Map<?, ?>) data.get("lead");
    assertThat(String.valueOf(lead.get("leadId"))).isEqualTo(leadId);
    assertThat(String.valueOf(lead.get("inquiryNo"))).startsWith("INQ-");
  }

  @Test
  void merchantLeadQuoteShouldWork() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/inquiries/merchant/leads/ML-IQ20260418001-S001/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "merchantId":"S001",
                      "supplierName":"唐山弘达钢贸",
                      "unitPrice":"3510.5",
                      "totalAmount":"421260.00",
                      "deliveryDays":"1",
                      "paymentTerm":"月结15天",
                      "canInvoice":"YES",
                      "quoteRemark":"可今天排产"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("QUOTED"));
  }

  @Test
  void merchantLeadStatusUpdateShouldValidateTransition() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/inquiries/merchant/leads/ML-IQ20260418001-S001/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "merchantId":"S001",
                      "status":"CONTACTED",
                      "operator":"李商务",
                      "remark":"已电话回访"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("CONTACTED"));
  }
}
