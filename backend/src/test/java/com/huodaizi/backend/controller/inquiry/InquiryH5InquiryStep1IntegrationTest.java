package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class InquiryH5InquiryStep1IntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void initShouldReturnDraftAndOptions() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/inquiries/h5/inquiry/step1/init").param("city", "唐山"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.city").value("唐山"))
            .andExpect(jsonPath("$.data.draftId").isNotEmpty())
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat((List<?>) data.get("categories")).isNotEmpty();
    assertThat((List<?>) data.get("deliveryCities")).isNotEmpty();
    assertThat(String.valueOf(data.get("tipText"))).isNotBlank();
  }

  @Test
  void saveShouldPersistStep1Draft() throws Exception {
    MvcResult initResult =
        mockMvc
            .perform(get("/api/v1/inquiries/h5/inquiry/step1/init").param("city", "无锡"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> initBody = objectMapper.readValue(initResult.getResponse().getContentAsString(), Map.class);
    String draftId = String.valueOf(((Map<?, ?>) initBody.get("data")).get("draftId"));

    String payload =
        """
        {
          "draftId":"%s",
          "categoryCode":"REBAR",
          "specText":"HRB400E Φ20*12m",
          "deliveryCity":"无锡",
          "demandQtyTon":120,
          "invoiceNeed":"YES",
          "contactMobile":"13812345678",
          "remark":"H5 step1 测试草稿"
        }
        """
            .formatted(draftId);

    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/inquiry/step1/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.draftId").value(draftId))
        .andExpect(jsonPath("$.data.draftStatus").value("STEP1_SAVED"))
        .andExpect(jsonPath("$.data.nextStepUrl").value("/h5/inquiry/step2?draftId=" + draftId));
  }

  @Test
  void saveShouldRejectInvalidMobile() throws Exception {
    String payload =
        """
        {
          "draftId":"H5DRAFT20260418001",
          "categoryCode":"REBAR",
          "specText":"HRB400E Φ20*12m",
          "deliveryCity":"唐山",
          "demandQtyTon":50,
          "invoiceNeed":"YES",
          "contactMobile":"1381",
          "remark":"invalid mobile"
        }
        """;
    mockMvc
        .perform(
            post("/api/v1/inquiries/h5/inquiry/step1/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
