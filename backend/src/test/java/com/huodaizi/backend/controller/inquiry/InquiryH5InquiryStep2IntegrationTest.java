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
class InquiryH5InquiryStep2IntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void step2InitShouldReturnDraftDetail() throws Exception {
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
          "demandQtyTon":100,
          "invoiceNeed":"YES",
          "contactMobile":"13812345678",
          "remark":"step1 done"
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

    mockMvc
        .perform(get("/api/v1/inquiries/h5/inquiry/step2/init").param("draftId", draftId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.draftId").value(draftId))
        .andExpect(jsonPath("$.data.specText").value("HRB400E Φ20*12m"))
        .andExpect(jsonPath("$.data.expectedDeliveryOptions").isArray())
        .andExpect(jsonPath("$.data.settleTypeOptions").isArray());
  }

  @Test
  void step2SubmitShouldCreateInquiry() throws Exception {
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
          "invoiceNeed":"ANY",
          "contactMobile":"13912345678",
          "remark":"step1 for step2 submit"
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
          "step2Remark":"优先当日装车"
        }
        """
            .formatted(draftId);
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/inquiry/step2/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(step2Payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.draftId").value(draftId))
        .andExpect(jsonPath("$.data.inquiryId").isNotEmpty())
        .andExpect(jsonPath("$.data.inquiryNo").isNotEmpty())
        .andExpect(jsonPath("$.data.successMessage").value("询价提交成功，系统正在为您匹配优质商家"));
  }

  @Test
  void step2SubmitShouldRejectInvalidNeedInvoice() throws Exception {
    String payload =
        """
        {
          "draftId":"H5DRAFT20260418001",
          "expectedDeliveryAt":"2026-05-01",
          "deliveryTimeRange":"09:00-18:00",
          "unloadSupport":"N",
          "needInvoice":"X",
          "step2Remark":"invalid"
        }
        """;
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/inquiry/step2/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
