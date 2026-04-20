package com.huodaizi.backend.controller.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class N11PaymentResultIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void paymentResultShouldShowAfterPay() throws Exception {
    String loginResp =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "account":"13800138000",
                          "password":"Demo@123456",
                          "contactMobile":"13800138000"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> body = mapper.readValue(loginResp, java.util.Map.class);
    java.util.Map<?, ?> data = (java.util.Map<?, ?>) body.get("data");
    String token = String.valueOf(data.get("token"));

    String listResp =
        mockMvc
            .perform(
                get("/api/v1/auth/cashier/orders")
                    .header("X-Auth-Token", token)
                    .param("pageNo", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    java.util.Map<?, ?> target = null;
    for (Object item : records) {
      java.util.Map<?, ?> record = (java.util.Map<?, ?>) item;
      if ("UNPAID".equals(String.valueOf(record.get("payStatus")))) {
        target = record;
        break;
      }
    }
    if (target == null && !records.isEmpty()) {
      target = (java.util.Map<?, ?>) records.get(0);
    }
    org.junit.jupiter.api.Assertions.assertNotNull(target, "收银单列表不能为空");
    String cashierOrderId = String.valueOf(target.get("cashierOrderId"));

    if ("UNPAID".equals(String.valueOf(target.get("payStatus")))) {
      mockMvc
          .perform(
              post("/api/v1/auth/cashier/orders/{cashierOrderId}/pay", cashierOrderId)
                  .header("X-Auth-Token", token)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      """
                      {
                        "payMethod":"ALIPAY",
                        "payerName":"测试付款员",
                        "remark":"N11结果页测试支付",
                        "operator":"n11-test"
                      }
                      """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value("0"))
          .andExpect(jsonPath("$.data.status").value("PAID"));
    }

    mockMvc
        .perform(
            get("/api/v1/auth/cashier/orders/{cashierOrderId}/result", cashierOrderId)
                .header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.cashierOrderId").value(cashierOrderId))
        .andExpect(jsonPath("$.data.resultStatus").value("SUCCESS"))
        .andExpect(jsonPath("$.data.resultStatusText").value("支付成功"))
        .andExpect(jsonPath("$.data.payStatus").value("PAID"))
        .andExpect(jsonPath("$.data.payStatusText").value("已支付"))
        .andExpect(jsonPath("$.data.linkedOrderStatus").value("COMPLETED"))
        .andExpect(jsonPath("$.data.nextActionHint").value("查看订单详情"))
        .andExpect(jsonPath("$.data.nodes").isArray());
  }
}
