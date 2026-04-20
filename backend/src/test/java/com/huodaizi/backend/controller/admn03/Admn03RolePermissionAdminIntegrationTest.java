package com.huodaizi.backend.controller.admn03;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class Admn03RolePermissionAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/rbac/roles"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailCreateAndPermissionUpdateShouldWork() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/rbac/roles")
                .header("X-Admin-Token", "test-admin-token")
                .param("status", "ACTIVE")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records").isArray())
        .andExpect(jsonPath("$.data.activeCount").isNumber())
        .andExpect(jsonPath("$.data.disabledCount").isNumber());

    String createResp =
        mockMvc
            .perform(
                post("/api/admin/rbac/roles")
                    .header("X-Admin-Token", "test-admin-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "roleCode":"OPS_AUDITOR",
                          "roleName":"运营审计员",
                          "description":"用于运营数据稽核与只读审批",
                          "permissionCodes":["DASHBOARD_VIEW","LEAD_OPS_MANAGE"],
                          "operator":"admn03-admin"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.roleCode").value("OPS_AUDITOR"))
            .andExpect(jsonPath("$.data.permissionCodes").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> createBody = mapper.readValue(createResp, java.util.Map.class);
    java.util.Map<?, ?> createData = (java.util.Map<?, ?>) createBody.get("data");
    String roleId = String.valueOf(createData.get("roleId"));

    mockMvc
        .perform(get("/api/admin/rbac/roles/{roleId}", roleId).header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.roleId").value(roleId))
        .andExpect(jsonPath("$.data.roleCode").value("OPS_AUDITOR"));

    mockMvc
        .perform(
            put("/api/admin/rbac/roles/{roleId}/permissions", roleId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "permissionCodes":["DASHBOARD_VIEW","RISK_ALERT_MANAGE","ADMN03_RBAC_MANAGE"],
                      "operator":"admn03-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.roleId").value(roleId))
        .andExpect(jsonPath("$.data.permissionCodes[0]").value("DASHBOARD_VIEW"))
        .andExpect(jsonPath("$.data.permissionCodes[1]").value("RISK_ALERT_MANAGE"))
        .andExpect(jsonPath("$.data.permissionCodes[2]").value("ADMN03_RBAC_MANAGE"));
  }
}
