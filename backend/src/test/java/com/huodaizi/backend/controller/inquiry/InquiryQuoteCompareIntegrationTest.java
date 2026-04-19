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
class InquiryQuoteCompareIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void quoteCompareShouldSupportDefaultSortAndFilters() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/inquiries/compare")
                    .param("inquiryId", "IQ20260418001")
                    .param("contactMobile", "13800138000")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(String.valueOf(data.get("inquiryId"))).isEqualTo("IQ20260418001");
    assertThat(((Number) data.get("total")).intValue()).isGreaterThan(0);

    List<?> items = (List<?>) data.get("quotes");
    assertThat(items).isNotEmpty();
    Map<?, ?> first = (Map<?, ?>) items.get(0);
    assertThat(String.valueOf(first.get("supplierName"))).isNotBlank();
    assertThat(String.valueOf(first.get("unitPrice"))).isNotBlank();
  }

  @Test
  void quoteCompareShouldSupportSortByPriceAndService() throws Exception {
    MvcResult byPrice =
        mockMvc
            .perform(
                get("/api/v1/inquiries/compare")
                    .param("inquiryId", "IQ20260418001")
                    .param("contactMobile", "13800138000")
                    .param("sortBy", "TOTAL_PRICE")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andReturn();
    Map<?, ?> priceBody = objectMapper.readValue(byPrice.getResponse().getContentAsString(), Map.class);
    Map<?, ?> priceData = (Map<?, ?>) priceBody.get("data");
    List<?> priceItems = (List<?>) priceData.get("quotes");
    assertThat(priceItems).isNotEmpty();

    MvcResult byService =
        mockMvc
            .perform(
                get("/api/v1/inquiries/compare")
                    .param("inquiryId", "IQ20260418001")
                    .param("contactMobile", "13800138000")
                    .param("sortBy", "SUPPLIER_SCORE")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andReturn();
    Map<?, ?> serviceBody = objectMapper.readValue(byService.getResponse().getContentAsString(), Map.class);
    Map<?, ?> serviceData = (Map<?, ?>) serviceBody.get("data");
    List<?> serviceItems = (List<?>) serviceData.get("quotes");
    assertThat(serviceItems).isNotEmpty();
  }

  @Test
  void quoteCompareShouldRejectInvalidSortBy() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/inquiries/compare")
                .param("inquiryId", "IQ20260418001")
                .param("contactMobile", "13800138000")
                .param("sortBy", "INVALID_SORT")
                .param("page", "1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void quoteCompareShouldReturnNotFoundWhenInquiryMissing() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/inquiries/compare")
                .param("inquiryId", "IQ99999999999")
                .param("contactMobile", "13800138000")
                .param("page", "1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("NOT_FOUND"));
  }
}
