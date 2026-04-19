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
class InquiryH5QuoteCompareIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void h5QuoteCompareShouldReturnQuotesAfterStep2Submit() throws Exception {
    MvcResult initStep1Result =
        mockMvc
            .perform(get("/api/v1/inquiries/h5/inquiry/step1/init").param("city", "无锡"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> step1Body = objectMapper.readValue(initStep1Result.getResponse().getContentAsString(), Map.class);
    String draftId = String.valueOf(((Map<?, ?>) step1Body.get("data")).get("draftId"));

    String step1Payload =
        """
        {
          "draftId":"%s",
          "categoryCode":"HOT_ROLL",
          "specText":"Q235B 3.0*1500*C",
          "deliveryCity":"无锡",
          "demandQtyTon":80,
          "invoiceNeed":"YES",
          "contactMobile":"13912345678",
          "remark":"h5 quote compare"
        }
        """
            .formatted(draftId);
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/inquiry/step1/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(step1Payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    String step2Payload =
        """
        {
          "draftId":"%s",
          "expectedDeliveryAt":"2026-05-01",
          "deliveryTimeRange":"09:00-18:00",
          "unloadSupport":"N",
          "needInvoice":"Y",
          "step2Remark":"for compare"
        }
        """
            .formatted(draftId);
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/inquiry/step2/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(step2Payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/h5/quote-compare")
                .param("draftId", draftId)
                .param("sortBy", "TOTAL_PRICE")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.draftId").value(draftId))
        .andExpect(jsonPath("$.data.selectedSortBy").value("TOTAL_PRICE"))
        .andExpect(jsonPath("$.data.inquiryId").isNotEmpty())
        .andExpect(jsonPath("$.data.inquiryNo").isNotEmpty())
        .andExpect(jsonPath("$.data.quotes").isArray())
        .andExpect(jsonPath("$.data.dealConfirmBaseUrl").value("/inquiry/deal/confirm"));
  }

  @Test
  void h5QuoteCompareShouldRejectUnsubmittedDraft() throws Exception {
    MvcResult initStep1Result =
        mockMvc
            .perform(get("/api/v1/inquiries/h5/inquiry/step1/init").param("city", "唐山"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> step1Body = objectMapper.readValue(initStep1Result.getResponse().getContentAsString(), Map.class);
    String draftId = String.valueOf(((Map<?, ?>) step1Body.get("data")).get("draftId"));

    mockMvc
        .perform(get("/api/v1/inquiries/h5/quote-compare").param("draftId", draftId))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
