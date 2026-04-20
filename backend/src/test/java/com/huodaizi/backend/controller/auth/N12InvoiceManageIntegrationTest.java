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
class N12InvoiceManageIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void invoiceTitleAndApplicationFlowShouldWork() throws Exception {
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

    String titleListResp =
        mockMvc
            .perform(get("/api/v1/auth/invoices/titles").header("X-Auth-Token", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();
    java.util.Map<?, ?> titleListBody = mapper.readValue(titleListResp, java.util.Map.class);
    java.util.Map<?, ?> titleListData = (java.util.Map<?, ?>) titleListBody.get("data");
    java.util.List<?> titles = (java.util.List<?>) titleListData.get("records");
    java.util.Map<?, ?> firstTitle = (java.util.Map<?, ?>) titles.get(0);
    String titleId = String.valueOf(firstTitle.get("titleId"));

    mockMvc
        .perform(
            post("/api/v1/auth/invoices/titles")
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "titleName":"演示钢贸有限公司上海分公司",
                      "taxNo":"91310101N120000001",
                      "address":"上海市浦东新区世纪大道100号",
                      "phone":"13800138001",
                      "bankName":"中国银行上海分行",
                      "bankAccountNo":"310101202600112233",
                      "defaultTitle":"Y",
                      "operator":"n12-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.titleName").value("演示钢贸有限公司上海分公司"))
        .andExpect(jsonPath("$.data.taxNo").value("91310101N120000001"));

    mockMvc
        .perform(
            post("/api/v1/auth/invoices/titles/{titleId}/default", titleId)
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "operator":"n12-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.titleId").value(titleId))
        .andExpect(jsonPath("$.data.defaultTitle").value(true));

    String createInvoiceResp =
        mockMvc
            .perform(
                post("/api/v1/auth/invoices/applications")
                    .header("X-Auth-Token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "orderId":"OD0001",
                          "titleId":"%s",
                          "invoiceContent":"货款",
                          "remark":"请在3个工作日内开具并邮寄",
                          "operator":"n12-test"
                        }
                        """
                            .formatted(titleId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.orderId").value("OD0001"))
            .andExpect(jsonPath("$.data.invoiceStatus").value("SUBMITTED"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> createInvoiceBody = mapper.readValue(createInvoiceResp, java.util.Map.class);
    java.util.Map<?, ?> createInvoiceData = (java.util.Map<?, ?>) createInvoiceBody.get("data");
    String applicationId = String.valueOf(createInvoiceData.get("invoiceApplyId"));

    mockMvc
        .perform(
            get("/api/v1/auth/invoices/applications")
                .header("X-Auth-Token", token)
                .param("status", "SUBMITTED")
                .param("pageNo", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records").isArray());

    mockMvc
        .perform(
            get("/api/v1/auth/invoices/applications/{applicationId}", applicationId)
                .header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.invoiceApplyId").value(applicationId))
        .andExpect(jsonPath("$.data.invoiceType").value("VAT_SPECIAL"))
        .andExpect(jsonPath("$.data.titleId").value(titleId));
  }
}
