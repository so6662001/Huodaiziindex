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
class InquiryH5InquiryStep3IntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void step3ShouldReturnSuccessSummaryAfterStep2Submit() throws Exception {
    MvcResult initStep1Result =
        mockMvc
            .perform(get("/api/v1/inquiries/h5/inquiry/step1/init").param("city", "唐山"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> step1Body = objectMapper.readValue(initStep1Result.getResponse().getContentAsString(), Map.class);
    String draftId = String.valueOf(((Map<?, ?>) step1Body.get("data")).get("draftId"));

    String step1Payload =
        """
        {
          "draftId":"%s",
          "categoryCode":"REBAR",
          "specText":"HRB400E Φ20*12m",
          "deliveryCity":"唐山",
          "demandQtyTon":120,
          "invoiceNeed":"YES",
          "contactMobile":"13812345678",
          "remark":"step1 for step3"
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
          "step2Remark":"step2 for step3"
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
        .perform(get("/api/v1/inquiries/h5/inquiry/step3").param("draftId", draftId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.draftId").value(draftId))
        .andExpect(jsonPath("$.data.inquiryId").isNotEmpty())
        .andExpect(jsonPath("$.data.inquiryNo").isNotEmpty())
        .andExpect(jsonPath("$.data.quoteCount").isNumber())
        .andExpect(jsonPath("$.data.nextSteps").isArray())
        .andExpect(jsonPath("$.data.compareUrl").isNotEmpty());
  }

  @Test
  void step3ShouldRejectUnsubmittedDraft() throws Exception {
    MvcResult initStep1Result =
        mockMvc
            .perform(get("/api/v1/inquiries/h5/inquiry/step1/init").param("city", "无锡"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> step1Body = objectMapper.readValue(initStep1Result.getResponse().getContentAsString(), Map.class);
    String draftId = String.valueOf(((Map<?, ?>) step1Body.get("data")).get("draftId"));

    mockMvc
        .perform(get("/api/v1/inquiries/h5/inquiry/step3").param("draftId", draftId))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
