package com.huodaizi.backend.controller.leadops;

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
class A02LeadOpsAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/lead-ops/leads"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listShouldSupportMixedSources() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/lead-ops/leads")
                .header("X-Admin-Token", "test-admin-token")
                .param("source", "ALL")
                .param("status", "NEW")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.data.overview.total").isNumber())
        .andExpect(jsonPath("$.data.source").value("ALL"));
  }

  @Test
  void assignStatusFollowShouldWorkForSiteAdLead() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/lead-ops/leads/SITE_AD/SAL20260418001/assign")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "ownerName":"运营A",
                      "team":"A组",
                      "operator":"运营A",
                      "comment":"分配并开始跟进"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.owner").value("运营A"));

    mockMvc
        .perform(
            put("/api/admin/lead-ops/leads/SITE_AD/SAL20260418001/status")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "status":"CONTACTED",
                      "operator":"运营A",
                      "comment":"已电话沟通"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("CONTACTED"));

    mockMvc
        .perform(
            post("/api/admin/lead-ops/leads/SITE_AD/SAL20260418001/follow")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "content":"已发送报价单",
                      "nextActionAt":"明日回访",
                      "operator":"运营A"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.latestFollow").isNotEmpty());
  }
}
