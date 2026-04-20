package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class InquiryReconcileOrderIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void createReconcileOrderShouldWork() throws Exception {
    MvcResult pickupCreateResult =
        mockMvc
            .perform(
                post("/api/v1/inquiries/pickup-orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "inquiryId":"IQ20260418001",
                          "quoteId":"IQ20260418001-Q1",
                          "contactMobile":"13800138000",
                          "pickupSite":"唐山丰润货场A区",
                          "pickupDate":"2026-04-20",
                          "pickupVehicleNo":"冀B12345",
                          "pickupDriverName":"王师傅",
                          "pickupDriverPhone":"13800001111",
                          "agreedProtocol":true
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> pickupCreateBody =
        objectMapper.readValue(pickupCreateResult.getResponse().getContentAsString(), Map.class);
    String pickupOrderId = String.valueOf(((Map<?, ?>) pickupCreateBody.get("data")).get("pickupId"));

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/inquiries/reconcile-orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "pickupOrderId":"%s",
                          "contactMobile":"13800138000",
                          "statementMonth":"2026-04",
                          "settleType":"MONTHLY",
                          "dueDate":"2026-05-20",
                          "invoiceTitle":"唐山测试采购有限公司",
                          "remark":"按合同结算"
                        }
                        """
                            .formatted(pickupOrderId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.reconcileId").isNotEmpty())
            .andExpect(jsonPath("$.data.status").value("CREATED"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(String.valueOf(data.get("reconcileNo"))).startsWith("RC-");
  }

  @Test
  void listAndDetailShouldWork() throws Exception {
    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/reconcile-orders")
                    .param("contactMobile", "13800138000")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> listBody = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> listData = (Map<?, ?>) listBody.get("data");
    if (((Number) listData.get("total")).intValue() == 0) {
      MvcResult pickupCreateResult =
          mockMvc
              .perform(
                  post("/api/v1/inquiries/pickup-orders")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(
                          """
                          {
                            "inquiryId":"IQ20260418001",
                            "quoteId":"IQ20260418001-Q1",
                            "contactMobile":"13800138000",
                            "pickupSite":"唐山丰润货场A区",
                            "pickupDate":"2026-04-20",
                            "pickupVehicleNo":"冀B12345",
                            "pickupDriverName":"王师傅",
                            "pickupDriverPhone":"13800001111",
                            "agreedProtocol":true
                          }
                          """))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.code").value("0"))
              .andReturn();
      Map<?, ?> pickupCreateBody =
          objectMapper.readValue(pickupCreateResult.getResponse().getContentAsString(), Map.class);
      String pickupOrderId = String.valueOf(((Map<?, ?>) pickupCreateBody.get("data")).get("pickupId"));
      mockMvc
          .perform(
              post("/api/v1/inquiries/reconcile-orders")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      """
                      {
                        "pickupOrderId":"%s",
                        "contactMobile":"13800138000",
                        "statementMonth":"2026-04",
                        "settleType":"MONTHLY",
                        "dueDate":"2026-05-20",
                        "invoiceTitle":"唐山测试采购有限公司",
                        "remark":"自动补单"
                      }
                      """
                          .formatted(pickupOrderId)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value("0"));

      listResult =
          mockMvc
              .perform(
                  get("/api/v1/inquiries/reconcile-orders")
                      .param("contactMobile", "13800138000")
                      .param("page", "1")
                      .param("pageSize", "10"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.code").value("0"))
              .andReturn();
      listBody = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
      listData = (Map<?, ?>) listBody.get("data");
    }

    assertThat(((Number) listData.get("total")).intValue()).isGreaterThanOrEqualTo(1);
    Object firstObj = ((java.util.List<?>) listData.get("items")).get(0);
    Map<?, ?> first = (Map<?, ?>) firstObj;
    String reconcileOrderId = String.valueOf(first.get("reconcileId"));

    mockMvc
        .perform(
            get("/api/v1/inquiries/reconcile-orders/{reconcileOrderId}", reconcileOrderId)
                .param("contactMobile", "13800138000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.reconcileId").value(reconcileOrderId));
  }

  @Test
  void updateStatusShouldSupportPaidFlow() throws Exception {
    MvcResult listResult =
        mockMvc
            .perform(
                get("/api/v1/inquiries/reconcile-orders")
                    .param("contactMobile", "13800138000")
                    .param("page", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> listBody = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> listData = (Map<?, ?>) listBody.get("data");
    if (((Number) listData.get("total")).intValue() == 0) {
      MvcResult pickupCreateResult =
          mockMvc
              .perform(
                  post("/api/v1/inquiries/pickup-orders")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(
                          """
                          {
                            "inquiryId":"IQ20260418001",
                            "quoteId":"IQ20260418001-Q1",
                            "contactMobile":"13800138000",
                            "pickupSite":"唐山丰润货场A区",
                            "pickupDate":"2026-04-20",
                            "pickupVehicleNo":"冀B12345",
                            "pickupDriverName":"王师傅",
                            "pickupDriverPhone":"13800001111",
                            "agreedProtocol":true
                          }
                          """))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.code").value("0"))
              .andReturn();
      Map<?, ?> pickupCreateBody =
          objectMapper.readValue(pickupCreateResult.getResponse().getContentAsString(), Map.class);
      String pickupOrderId = String.valueOf(((Map<?, ?>) pickupCreateBody.get("data")).get("pickupId"));
      mockMvc
          .perform(
              post("/api/v1/inquiries/reconcile-orders")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      """
                      {
                        "pickupOrderId":"%s",
                        "contactMobile":"13800138000",
                        "statementMonth":"2026-04",
                        "settleType":"MONTHLY",
                        "dueDate":"2026-05-20",
                        "invoiceTitle":"唐山测试采购有限公司",
                        "remark":"自动补单"
                      }
                      """
                          .formatted(pickupOrderId)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value("0"));

      listResult =
          mockMvc
              .perform(
                  get("/api/v1/inquiries/reconcile-orders")
                      .param("contactMobile", "13800138000")
                      .param("page", "1")
                      .param("pageSize", "10"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.code").value("0"))
              .andReturn();
      listBody = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
      listData = (Map<?, ?>) listBody.get("data");
    }
    Object firstObj = ((java.util.List<?>) listData.get("items")).get(0);
    Map<?, ?> first = (Map<?, ?>) firstObj;
    String reconcileOrderId = String.valueOf(first.get("reconcileId"));

    mockMvc
        .perform(
            put("/api/v1/inquiries/reconcile-orders/{reconcileOrderId}/status", reconcileOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "contactMobile":"13800138000",
                      "status":"PAID",
                      "operator":"财务A",
                      "paidAmount":"420000",
                      "remark":"已全额回款"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.order.status").value("PAID"));
  }
}
