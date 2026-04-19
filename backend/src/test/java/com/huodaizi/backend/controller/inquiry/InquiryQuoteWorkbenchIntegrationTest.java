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
class InquiryQuoteWorkbenchIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void workbenchOverviewShouldReturnStats() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/inquiries/merchant/workbench/overview").param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(String.valueOf(data.get("merchantId"))).isEqualTo("S001");
    assertThat(((Number) data.get("pendingQuoteCount")).intValue()).isGreaterThanOrEqualTo(0);
    assertThat(String.valueOf(data.get("avgResponseMinutes"))).isNotBlank();
  }

  @Test
  void workbenchTaskListShouldSupportFiltersAndPaging() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/inquiries/merchant/workbench/tasks")
                    .param("merchantId", "S001")
                    .param("status", "NEW")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    List<?> items = (List<?>) data.get("items");
    assertThat(items).isNotNull();
    assertThat(((Number) data.get("total")).intValue()).isGreaterThanOrEqualTo(0);
  }

  @Test
  void workbenchBatchUpdateShouldMarkTasks() throws Exception {
    String payload =
        """
        {
          "merchantId":"S001",
          "leadIds":["ML-IQ20260418001-S001"],
          "status":"CONTACTED",
          "operator":"李商务",
          "comment":"工作台批量处理"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/inquiries/merchant/workbench/tasks/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.items[0].status").value("CONTACTED"))
        .andExpect(jsonPath("$.data.items[0].latestRemark").value("工作台批量处理"));
  }

  @Test
  void workbenchBatchUpdateShouldRejectWhenLeadIdsEmpty() throws Exception {
    String payload =
        """
        {
          "merchantId":"S001",
          "leadIds":[],
          "status":"CONTACTED"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/inquiries/merchant/workbench/tasks/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
