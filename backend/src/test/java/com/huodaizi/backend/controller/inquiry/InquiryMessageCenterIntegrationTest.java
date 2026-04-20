package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class InquiryMessageCenterIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void listMessagesShouldReturnItems() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/inquiries/merchant/messages").param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.merchantId").value("S001"))
            .andExpect(jsonPath("$.data.items").isArray())
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat((List<?>) data.get("items")).isNotEmpty();
  }

  @Test
  void messageDetailShouldWork() throws Exception {
    MvcResult listResult =
        mockMvc
            .perform(get("/api/v1/inquiries/merchant/messages").param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andReturn();
    Map<?, ?> listBody = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> listData = (Map<?, ?>) listBody.get("data");
    Map<?, ?> first = (Map<?, ?>) ((List<?>) listData.get("items")).get(0);
    String messageId = String.valueOf(first.get("messageId"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/merchant/messages/{messageId}", messageId).param("merchantId", "S001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.message.messageId").value(messageId));
  }

  @Test
  void readSingleAndReadAllShouldWork() throws Exception {
    MvcResult listResult =
        mockMvc
            .perform(get("/api/v1/inquiries/merchant/messages").param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andReturn();
    Map<?, ?> listBody = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> listData = (Map<?, ?>) listBody.get("data");
    Map<?, ?> first = (Map<?, ?>) ((List<?>) listData.get("items")).get(0);
    String messageId = String.valueOf(first.get("messageId"));

    mockMvc
        .perform(
            put("/api/v1/inquiries/merchant/messages/{messageId}/read", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "merchantId":"S001"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.messageId").value(messageId))
        .andExpect(jsonPath("$.data.status").value("READ"));

    mockMvc
        .perform(
            put("/api/v1/inquiries/merchant/messages/read-all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "merchantId":"S001"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("READ"))
        .andExpect(jsonPath("$.data.message").value("已全部标记为已读"));
  }
}
