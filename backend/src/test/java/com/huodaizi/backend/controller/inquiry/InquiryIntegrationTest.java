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
class InquiryIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void createInquiryAndListShouldWork() throws Exception {
    String payload =
        """
        {
          "categoryCode":"rebar",
          "specText":"HRB400E Φ20*12m",
          "demandQtyTon":120,
          "deliveryCity":"唐山",
          "expectedDeliveryDate":"2026-04-25",
          "invoiceNeed":"YES",
          "contactMobile":"13811112222",
          "remark":"优先当日可装车"
        }
        """;

    MvcResult createResult =
        mockMvc
            .perform(post("/api/v1/inquiries").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.inquiryId").isNotEmpty())
            .andReturn();

    Map<?, ?> createBody = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> createData = (Map<?, ?>) createBody.get("data");
    assertThat(String.valueOf(createData.get("inquiryNo"))).startsWith("INQ-");
    assertThat(String.valueOf(createData.get("inquiryStatus"))).isEqualTo("OPEN");

    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries")
                    .param("contactMobile", "13811112222")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> listBody = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> listData = (Map<?, ?>) listBody.get("data");
    List<?> items = (List<?>) listData.get("items");
    assertThat(items).isNotEmpty();
    Map<?, ?> first = (Map<?, ?>) items.get(0);
    assertThat(String.valueOf(first.get("deliveryCity"))).isNotBlank();
    assertThat(String.valueOf(first.get("demandQtyTon"))).isEqualTo("120");
  }

  @Test
  void createInquiryShouldRejectInvalidMobileAndQty() throws Exception {
    String payload =
        """
        {
          "categoryCode":"rebar",
          "specText":"HRB400E Φ20*12m",
          "demandQtyTon":0,
          "deliveryCity":"唐山",
          "contactMobile":"1381111"
        }
        """;
    mockMvc
        .perform(post("/api/v1/inquiries").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void listInquiryShouldRequireValidMobile() throws Exception {
    mockMvc
        .perform(get("/api/v1/inquiries").param("contactMobile", "1381").param("page", "1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
