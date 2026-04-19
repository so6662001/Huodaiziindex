package com.huodaizi.backend.controller.dispatchstrategy;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class A06DispatchStrategyAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/dispatch-strategy/rules"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listShouldWorkWithValidToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/dispatch-strategy/rules")
                .header("X-Admin-Token", "test-admin-token")
                .param("sceneCode", "MERCHANT_LEAD"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").isNumber())
        .andExpect(jsonPath("$.data.items").isArray());
  }

  @Test
  void updateShouldWork() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/dispatch-strategy/rules/MERCHANT_LEAD")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "sceneCode":"MERCHANT_LEAD",
                      "ruleVersion":"v2026.05",
                      "sceneName":"线索分发评分规则-A06",
                      "scoreFormula":"基础分100 + 履约35 + 响应25 + 价格20 - 风险20",
                      "updateCycle":"每周滚动更新",
                      "paidFactorDesc":"付费因素仅小幅加成（A06）",
                      "dimensions":[
                        {"code":"FULFILLMENT","name":"履约稳定性","weight":35,"description":"近90天交付评估","scoreMethod":"正向","dataSource":"履约中心"},
                        {"code":"RESPONSE","name":"响应时效","weight":25,"description":"首次响应时长","scoreMethod":"正向","dataSource":"报价工作台"},
                        {"code":"PRICE_COMP","name":"报价竞争力","weight":20,"description":"报价偏离中位值","scoreMethod":"正向","dataSource":"报价对比"},
                        {"code":"DISPUTE","name":"纠纷率","weight":15,"description":"争议占比","scoreMethod":"负向","dataSource":"对账中心"},
                        {"code":"DATA_QUALITY","name":"数据完整性","weight":5,"description":"回单与凭证完整度","scoreMethod":"正向","dataSource":"提货/对账"}
                      ],
                      "bonuses":[
                        {"code":"B01","name":"连续0纠纷","scoreChange":"+3","trigger":"自然月新增订单无争议","cap":"每月最多加1次"},
                        {"code":"B02","name":"T+1回单","scoreChange":"+2","trigger":"签收后1日回单完整","cap":"每周最多加2次"}
                      ],
                      "penalties":[
                        {"code":"P01","name":"超时未报价","scoreChange":"-5","trigger":"有效线索超时未报价","recovery":"次周连续响应达标"},
                        {"code":"P02","name":"履约违约","scoreChange":"-8","trigger":"成交后拒发货","recovery":"人工复核后恢复"}
                      ],
                      "disclosures":[
                        "核心维度公开透明",
                        "付费因素仅小幅加成"
                      ],
                      "operator":"a06-admin",
                      "remark":"A06策略调优"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.sceneCode").value("MERCHANT_LEAD"))
        .andExpect(jsonPath("$.data.sceneName").value("线索分发评分规则-A06"));
  }
}
