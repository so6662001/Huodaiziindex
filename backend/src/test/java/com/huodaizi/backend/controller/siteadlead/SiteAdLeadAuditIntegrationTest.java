package com.huodaizi.backend.controller.siteadlead;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "huodaizi.admin.auth.enabled=true",
      "huodaizi.admin.auth.token=test-admin-token"
    })
class SiteAdLeadAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void submitAndMineShouldWorkWithMaskedContactAndValidResponseFields() throws Exception {
    String payload =
        """
        {
          "placementId":"SA009",
          "placementName":"分站信息流广告位",
          "city":"唐山",
          "duration":"30天",
          "budget":"5000-8000",
          "companyName":"唐山钢贸有限公司",
          "contactName":"王经理",
          "contactPhone":"13812345678",
          "remark":"希望下周上线",
          "agreed":true
        }
        """;

    MvcResult submitResult =
        mockMvc
            .perform(
                post("/api/v1/site-ad-lead/submit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.leadNo").isNotEmpty())
            .andReturn();

    Map<?, ?> submitBody = objectMapper.readValue(submitResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> submitData = (Map<?, ?>) submitBody.get("data");
    String leadId = String.valueOf(submitData.get("leadId"));
    assertThat(leadId).startsWith("SAL");

    MvcResult mineResult =
        mockMvc
            .perform(get("/api/v1/site-ad-lead/mine").param("contactPhone", "13812345678"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> mineBody = objectMapper.readValue(mineResult.getResponse().getContentAsString(), Map.class);
    Map<?, ?> mineData = (Map<?, ?>) mineBody.get("data");
    List<?> items = (List<?>) mineData.get("items");
    assertThat(items).isNotEmpty();
    Map<?, ?> item = (Map<?, ?>) items.get(0);
    assertThat(String.valueOf(item.get("contactPhoneMasked"))).contains("****");
    assertThat(String.valueOf(item.get("status"))).isNotBlank();
  }

  @Test
  void mineShouldRequireExactPhoneAndSupportPaging() throws Exception {
    mockMvc
        .perform(get("/api/v1/site-ad-lead/mine").param("contactPhone", "13800"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));

    MvcResult page1 =
        mockMvc
            .perform(
                get("/api/v1/site-ad-lead/mine")
                    .param("contactPhone", "13800138000")
                    .param("page", "1")
                    .param("pageSize", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> page1Body = objectMapper.readValue(page1.getResponse().getContentAsString(), Map.class);
    Map<?, ?> page1Data = (Map<?, ?>) page1Body.get("data");
    assertThat((List<?>) page1Data.get("items")).hasSize(1);
    assertThat(((Number) page1Data.get("total")).intValue()).isGreaterThanOrEqualTo(1);
  }

  @Test
  void adminEndpointShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/site-ad-lead"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void adminEndpointShouldRejectWhenTokenInvalid() throws Exception {
    mockMvc
        .perform(get("/api/admin/site-ad-lead").header("X-Admin-Token", "wrong-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void adminEndpointShouldAllowWhenTokenValid() throws Exception {
    mockMvc
        .perform(get("/api/admin/site-ad-lead").header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void adminUpdateStatusShouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/site-ad-lead/SAL20260418001/status")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\",\"operatorName\":\"李商务\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void adminFollowShouldReturnBadRequestWhenContentMissing() throws Exception {
    mockMvc
        .perform(
            post("/api/admin/site-ad-lead/SAL20260418001/follow")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"operator\":\"李商务\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void adminListShouldFilterByStatusAndPhone() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/admin/site-ad-lead")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("status", "CONTACTED")
                    .param("phone", "1380013"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    List<?> items = (List<?>) data.get("items");
    assertThat(items).isNotEmpty();
    assertThat(((Map<?, ?>) items.get(0)).get("status")).isEqualTo("CONTACTED");
  }

  @Test
  void adminFollowLogsShouldContainAuditFields() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/admin/site-ad-lead/SAL20260418001/follow").header("X-Admin-Token", "test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();
    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    List<?> data = (List<?>) body.get("data");
    assertThat(data).isNotEmpty();
    Map<?, ?> first = (Map<?, ?>) data.get(0);
    assertThat(String.valueOf(first.get("id"))).startsWith("SALF");
    assertThat(String.valueOf(first.get("leadId"))).startsWith("SAL");
    assertThat(String.valueOf(first.get("action"))).isNotBlank();
    assertThat(String.valueOf(first.get("content"))).isNotBlank();
  }

  @Test
  void adminAllEndpointsShouldRequireToken() throws Exception {
    String payload =
        """
        {
          "placementId":"SA777",
          "placementName":"权限矩阵广告位",
          "city":"武汉",
          "duration":"7天",
          "budget":"1000-2000",
          "companyName":"权限矩阵有限公司",
          "contactName":"赵经理",
          "contactPhone":"13712345678",
          "remark":"用于权限矩阵测试",
          "agreed":true
        }
        """;
    MvcResult submitResult =
        mockMvc
            .perform(
                post("/api/v1/site-ad-lead/submit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isOk())
            .andReturn();
    Map<?, ?> submitBody = objectMapper.readValue(submitResult.getResponse().getContentAsString(), Map.class);
    String id = String.valueOf(((Map<?, ?>) submitBody.get("data")).get("leadId"));

    mockMvc.perform(get("/api/admin/site-ad-lead")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/api/admin/site-ad-lead/" + id)).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/api/admin/site-ad-lead/" + id + "/follow")).andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            put("/api/admin/site-ad-lead/" + id + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONTACTED\",\"operatorName\":\"测\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            put("/api/admin/site-ad-lead/" + id + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ownerName\":\"商务A\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post("/api/admin/site-ad-lead/" + id + "/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"跟进\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void concurrentSubmitShouldGenerateUniqueLeadIdsAndNos() throws Exception {
    int concurrent = 20;
    ExecutorService pool = Executors.newFixedThreadPool(8);
    try {
      List<Callable<Map<?, ?>>> tasks = new ArrayList<>();
      for (int i = 0; i < concurrent; i++) {
        final int idx = i;
        tasks.add(
            () -> {
              String phone = String.format("1360000%04d", idx);
              String payload =
                  """
                  {
                    "placementId":"SA900",
                    "placementName":"并发压测广告位",
                    "city":"唐山",
                    "duration":"15天",
                    "budget":"2000-3000",
                    "companyName":"并发测试公司%02d",
                    "contactName":"并发测",
                    "contactPhone":"%s",
                    "remark":"并发压测",
                    "agreed":true
                  }
                  """
                      .formatted(idx, phone);
              MvcResult result =
                  mockMvc
                      .perform(
                          post("/api/v1/site-ad-lead/submit")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(payload))
                      .andExpect(status().isOk())
                      .andReturn();
              Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
              return (Map<?, ?>) body.get("data");
            });
      }

      List<Future<Map<?, ?>>> futures = pool.invokeAll(tasks);
      List<String> ids = new ArrayList<>();
      List<String> nos = new ArrayList<>();
      for (Future<Map<?, ?>> future : futures) {
        Map<?, ?> data = future.get();
        ids.add(String.valueOf(data.get("leadId")));
        nos.add(String.valueOf(data.get("leadNo")));
      }
      assertThat(ids).doesNotHaveDuplicates();
      assertThat(nos).doesNotHaveDuplicates();
      assertThat(ids).allMatch(id -> id.startsWith("SAL"));
      assertThat(nos).allMatch(no -> no.startsWith("ADL-"));
    } finally {
      pool.shutdownNow();
    }
  }
}
