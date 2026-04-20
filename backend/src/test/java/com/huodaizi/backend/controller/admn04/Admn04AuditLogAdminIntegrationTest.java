package com.huodaizi.backend.controller.admn04;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "huodaizi.admin.auth.enabled=true",
      "huodaizi.admin.auth.token=test-admin-token"
    })
class Admn04AuditLogAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/audit-logs"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listAndDetailShouldWorkAndContainAdmnActions() throws Exception {
    String certListResp =
        mockMvc
            .perform(
                get("/api/admin/merchant-certifications")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> certListBody = mapper.readValue(certListResp, java.util.Map.class);
    java.util.Map<?, ?> certListData = (java.util.Map<?, ?>) certListBody.get("data");
    java.util.List<?> certRecords = (java.util.List<?>) certListData.get("records");
    if (!certRecords.isEmpty()) {
      String certificationId = String.valueOf(((java.util.Map<?, ?>) certRecords.get(0)).get("certificationId"));
      mockMvc
          .perform(
              put("/api/admin/merchant-certifications/{certificationId}/review", certificationId)
                  .header("X-Admin-Token", "test-admin-token")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      """
                      {
                        "action":"APPROVE",
                        "reviewRemark":"ADM-N04测试触发认证审核审计",
                        "reviewer":"admn04-admin"
                      }
                      """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value("0"));
    }

    mockMvc
        .perform(
            put("/api/admin/buyers/{userId}/blacklist", "U000000000021")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"BLACKLIST",
                      "reasonCode":"RISK_CONTROL",
                      "remark":"ADM-N04测试触发审计日志",
                      "operator":"admn04-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    String roleResp =
        mockMvc
            .perform(
                post("/api/admin/rbac/roles")
                    .header("X-Admin-Token", "test-admin-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "roleCode":"ADMN04_VIEWER",
                          "roleName":"审计日志查看员",
                          "description":"用于ADM-N04测试",
                          "permissionCodes":["ADMN03_RBAC_MANAGE","DASHBOARD_VIEW"],
                          "operator":"admn04-admin"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> roleBody = mapper.readValue(roleResp, java.util.Map.class);
    java.util.Map<?, ?> roleData = (java.util.Map<?, ?>) roleBody.get("data");
    String roleId = String.valueOf(roleData.get("roleId"));

    mockMvc
        .perform(
            put("/api/admin/rbac/roles/{roleId}/permissions", roleId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "permissionCodes":["ADMN03_RBAC_MANAGE","ADMN01_CERT_REVIEW","DASHBOARD_VIEW"],
                      "operator":"admn04-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    String listRespAdmn03 =
        mockMvc
            .perform(
                get("/api/admin/audit-logs")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("moduleCode", "ADMN03")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThan(0)))
            .andExpect(jsonPath("$.data.successCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> listBody = mapper.readValue(listRespAdmn03, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    java.util.Map<?, ?> first = (java.util.Map<?, ?>) records.get(0);
    String logId = String.valueOf(first.get("logId"));

    mockMvc
        .perform(get("/api/admin/audit-logs/{logId}", logId).header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.auditId").value(logId))
        .andExpect(jsonPath("$.data.moduleCode").isNotEmpty())
        .andExpect(jsonPath("$.data.tags").isArray());

    mockMvc
        .perform(
            get("/api/admin/audit-logs")
                .header("X-Admin-Token", "test-admin-token")
                .param("moduleCode", "ADMN01")
                .param("actionCode", "CERT_REVIEW")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records").isArray())
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThan(0)))
        .andExpect(jsonPath("$.data.records[0].moduleCode").value("ADMN01"));

    mockMvc
        .perform(
            get("/api/admin/audit-logs")
                .header("X-Admin-Token", "test-admin-token")
                .param("moduleCode", "ADMN02")
                .param("actionCode", "BUYER_BLACKLIST")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records").isArray())
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThan(0)))
        .andExpect(jsonPath("$.data.records[0].moduleCode").value("ADMN02"));
  }
}
