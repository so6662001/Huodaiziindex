package com.huodaizi.backend.controller.admn05;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class Admn05CategorySpecDictAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/category-spec-dicts"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndUpsertShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/category-spec-dicts")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("status", "ACTIVE")
                    .param("sceneCode", "BUYER_INQUIRY")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.activeCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    java.util.Map<?, ?> first = (java.util.Map<?, ?>) records.get(0);
    String dictId = String.valueOf(first.get("dictId"));

    mockMvc
        .perform(
            get("/api/admin/category-spec-dicts/{dictId}", dictId)
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.dictId").value(dictId))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    String upsertResp =
        mockMvc
            .perform(
                post("/api/admin/category-spec-dicts")
                    .header("X-Admin-Token", "test-admin-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "categoryCode":"WIRE_ROD",
                          "categoryName":"线材",
                          "specName":"材质",
                          "specValue":"Q195 Φ6.5",
                          "sceneCode":"MERCHANT_QUOTE",
                          "status":"ACTIVE",
                          "sortNo":8,
                          "remark":"ADM-N05测试新增词库项",
                          "operator":"admn05-admin"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.categoryCode").value("WIRE_ROD"))
            .andExpect(jsonPath("$.data.sceneCode").value("MERCHANT_QUOTE"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> upsertBody = mapper.readValue(upsertResp, java.util.Map.class);
    java.util.Map<?, ?> upsertData = (java.util.Map<?, ?>) upsertBody.get("data");
    String createdDictId = String.valueOf(upsertData.get("dictId"));

    mockMvc
        .perform(
            post("/api/admin/category-spec-dicts")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "categoryCode":"WIRE_ROD",
                      "categoryName":"线材",
                      "specName":"材质",
                      "specValue":"Q195 Φ6.5",
                      "sceneCode":"MERCHANT_QUOTE",
                      "status":"DISABLED",
                      "sortNo":12,
                      "remark":"ADM-N05测试更新词库项",
                      "operator":"admn05-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.dictId").value(createdDictId))
        .andExpect(jsonPath("$.data.status").value("DISABLED"))
        .andExpect(jsonPath("$.data.availableActions[1]").value("ENABLE"));

    mockMvc
        .perform(
            get("/api/admin/category-spec-dicts")
                .header("X-Admin-Token", "test-admin-token")
                .param("keyword", "Q195")
                .param("sceneCode", "MERCHANT_QUOTE")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)))
        .andExpect(jsonPath("$.data.records[0].sceneCode").value("MERCHANT_QUOTE"));
  }
}
