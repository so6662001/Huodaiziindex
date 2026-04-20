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
class N08AfterSaleDisputeIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listCreateDetailAndStatusShouldWork() throws Exception {
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

    mockMvc
        .perform(
            get("/api/v1/auth/after-sales/disputes")
                .header("X-Auth-Token", token)
                .param("pageNo", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records").isArray());

    String createResp =
        mockMvc
            .perform(
                post("/api/v1/auth/after-sales/disputes")
                    .header("X-Auth-Token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "orderId":"OD0001",
                          "issueType":"QUALITY",
                          "issueSummary":"到货批次存在偏差",
                          "issueDescription":"抽检发现2卷板厚度偏差超出约定范围。",
                          "expectedResolution":"申请补差并安排第三方复检",
                          "contactName":"售后经理",
                          "contactPhone":"13800138000",
                          "evidenceFiles":"https://cdn.huodaizi.com/dispute/new-evidence.png",
                          "operator":"n08-test"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.orderId").value("OD0001"))
            .andExpect(jsonPath("$.data.status").value("SUBMITTED"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> createBody = mapper.readValue(createResp, java.util.Map.class);
    java.util.Map<?, ?> createData = (java.util.Map<?, ?>) createBody.get("data");
    String disputeId = String.valueOf(createData.get("disputeId"));

    mockMvc
        .perform(
            get("/api/v1/auth/after-sales/disputes/{disputeId}", disputeId)
                .header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.disputeId").value(disputeId))
        .andExpect(jsonPath("$.data.issueType").value("QUALITY"));

    mockMvc
        .perform(
            post("/api/v1/auth/after-sales/disputes/{disputeId}/status", disputeId)
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"MARK_RESOLVED",
                      "remark":"双方已确认补差并结案",
                      "operator":"n08-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("RESOLVED"))
        .andExpect(jsonPath("$.data.statusText").value("已解决"));
  }
}
