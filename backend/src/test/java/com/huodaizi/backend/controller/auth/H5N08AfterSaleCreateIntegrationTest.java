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
class H5N08AfterSaleCreateIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void h5AfterSaleListCreateDetailAndStatusShouldWork() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/h5/send-login-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "mobile":"13800138000",
                      "operator":"h5-n08-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    String loginResp =
        mockMvc
            .perform(
                post("/api/v1/auth/h5/quick-login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "mobile":"13800138000",
                          "smsCode":"123456",
                          "channel":"H5",
                          "operator":"h5-n08-test"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.channel").value("H5"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> body = mapper.readValue(loginResp, java.util.Map.class);
    java.util.Map<?, ?> data = (java.util.Map<?, ?>) body.get("data");
    String token = String.valueOf(data.get("token"));

    mockMvc
        .perform(
            get("/api/v1/auth/h5/after-sales")
                .header("X-Auth-Token", token)
                .param("pageNo", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.records").isArray());

    String createResp =
        mockMvc
            .perform(
                post("/api/v1/auth/h5/after-sales")
                    .header("X-Auth-Token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "orderId":"OD0001",
                          "issueType":"QUALITY",
                          "issueSummary":"H5端抽检存在偏差",
                          "issueDescription":"抽检发现2卷板厚度偏差超过合同阈值。",
                          "expectedResolution":"申请补差并安排复检",
                          "contactName":"移动端售后",
                          "contactPhone":"13800138000",
                          "evidenceFiles":"https://cdn.huodaizi.com/dispute/h5-evidence.png",
                          "operator":"h5-n08-test"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.channel").value("H5"))
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
            get("/api/v1/auth/h5/after-sales/{disputeId}", disputeId)
                .header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.disputeId").value(disputeId))
        .andExpect(jsonPath("$.data.issueType").value("QUALITY"));

    mockMvc
        .perform(
            post("/api/v1/auth/h5/after-sales/{disputeId}/status", disputeId)
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"MARK_PROCESSING",
                      "remark":"H5端已同步至售后专员",
                      "operator":"h5-n08-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("PROCESSING"))
        .andExpect(jsonPath("$.data.statusText").value("处理中"));
  }

  @Test
  void h5AfterSaleApisShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/v1/auth/h5/after-sales"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
