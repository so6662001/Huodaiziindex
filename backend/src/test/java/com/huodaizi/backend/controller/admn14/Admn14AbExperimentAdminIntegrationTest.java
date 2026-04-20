package com.huodaizi.backend.controller.admn14;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class Admn14AbExperimentAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/ab-experiments"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndUpsertShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/ab-experiments")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("experimentStatus", "RUNNING")
                    .param("scenarioCode", "LEAD_DISPATCH")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.runningCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    String experimentId = String.valueOf(((java.util.Map<?, ?>) records.get(0)).get("experimentId"));

    mockMvc
        .perform(
            get("/api/admin/ab-experiments/{experimentId}", experimentId)
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.experimentId").value(experimentId))
        .andExpect(jsonPath("$.data.metrics").isArray())
        .andExpect(jsonPath("$.data.availableActions").isArray());

    String upsertResp =
        mockMvc
            .perform(
                put("/api/admin/ab-experiments")
                    .header("X-Admin-Token", "test-admin-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "experimentCode":"AB_LEAD_DISPATCH_BIAS_2026Q2",
                          "experimentName":"线索分发权重偏置优化实验",
                          "scenarioCode":"LEAD_DISPATCH",
                          "targetMetricCode":"QUOTE_TO_DEAL_RATE",
                          "trafficPercent":"35",
                          "baselineValue":"23.8%",
                          "targetValue":"27.1%",
                          "experimentStatus":"RUNNING",
                          "optimizationStage":"SCALING",
                          "startDate":"2026-04-20",
                          "endDate":"2026-05-31",
                          "owner":"算法策略组",
                          "metrics":[
                            {
                              "metricCode":"QUOTE_TO_DEAL_RATE",
                              "metricName":"报价转成交率",
                              "controlValue":"23.8%",
                              "variantValue":"27.1%",
                              "upliftRate":"13.87%",
                              "confidenceLevel":"99.1%"
                            },
                            {
                              "metricCode":"LEAD_RESPONSE_SPEED",
                              "metricName":"线索响应时效",
                              "controlValue":"11.2分钟",
                              "variantValue":"9.7分钟",
                              "upliftRate":"13.39%",
                              "confidenceLevel":"97.8%"
                            }
                          ],
                          "remark":"持续优化第二阶段，扩大流量并观察成交稳态",
                          "operator":"admn14-admin"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.experimentCode").value("AB_LEAD_DISPATCH_BIAS_2026Q2"))
            .andExpect(jsonPath("$.data.experimentStatus").value("RUNNING"))
            .andExpect(jsonPath("$.data.optimizationStage").value("SCALING"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> upsertBody = mapper.readValue(upsertResp, java.util.Map.class);
    java.util.Map<?, ?> upsertData = (java.util.Map<?, ?>) upsertBody.get("data");
    String createdExperimentId = String.valueOf(upsertData.get("experimentId"));

    mockMvc
        .perform(
            put("/api/admin/ab-experiments")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "experimentCode":"AB_LEAD_DISPATCH_BIAS_2026Q2",
                      "experimentName":"线索分发权重偏置优化实验",
                      "scenarioCode":"LEAD_DISPATCH",
                      "targetMetricCode":"QUOTE_TO_DEAL_RATE",
                      "trafficPercent":"35",
                      "baselineValue":"23.8%",
                      "targetValue":"28.2%",
                      "experimentStatus":"COMPLETED",
                      "optimizationStage":"RELEASED",
                      "startDate":"2026-04-20",
                      "endDate":"2026-05-31",
                      "owner":"算法策略组",
                      "metrics":[
                        {
                          "metricCode":"QUOTE_TO_DEAL_RATE",
                          "metricName":"报价转成交率",
                          "controlValue":"23.8%",
                          "variantValue":"28.2%",
                          "upliftRate":"18.49%",
                          "confidenceLevel":"99.4%"
                        }
                      ],
                      "remark":"实验完成并进入全量发布阶段",
                      "operator":"admn14-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.experimentId").value(createdExperimentId))
        .andExpect(jsonPath("$.data.experimentStatus").value("COMPLETED"))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            get("/api/admin/ab-experiments")
                .header("X-Admin-Token", "test-admin-token")
                .param("keyword", "线索分发权重偏置优化实验")
                .param("optimizationStage", "RELEASED")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)))
        .andExpect(jsonPath("$.data.records[0].scenarioCode").value("LEAD_DISPATCH"));
  }
}
