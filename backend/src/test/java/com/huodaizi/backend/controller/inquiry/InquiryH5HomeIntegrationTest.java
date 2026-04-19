package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class InquiryH5HomeIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void h5HomeShouldReturnCards() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/inquiries/h5/home")
                    .param("city", "唐山")
                    .param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.city").value("唐山"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat((List<?>) data.get("quickNavs")).isNotEmpty();
    assertThat((List<?>) data.get("banners")).isNotEmpty();
    assertThat((List<?>) data.get("marketCards")).isNotEmpty();
    assertThat((List<?>) data.get("recommendations")).isNotEmpty();
  }

  @Test
  void h5HomeShouldDefaultChannelWhenBlank() throws Exception {
    mockMvc
        .perform(get("/api/v1/inquiries/h5/home").param("city", "无锡"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.city").value("无锡"));
  }
}
