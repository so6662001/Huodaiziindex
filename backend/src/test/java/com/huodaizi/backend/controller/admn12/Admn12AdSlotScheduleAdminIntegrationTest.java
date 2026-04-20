package com.huodaizi.backend.controller.admn12;

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
class Admn12AdSlotScheduleAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/ad-slot-schedules"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndUpsertShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/ad-slot-schedules")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("scheduleStatus", "ACTIVE")
                    .param("cityCode", "NORTH_CHINA")
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
    String scheduleId = String.valueOf(((java.util.Map<?, ?>) records.get(0)).get("scheduleId"));

    mockMvc
        .perform(
            get("/api/admin/ad-slot-schedules/{scheduleId}", scheduleId)
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.scheduleId").value(scheduleId))
        .andExpect(jsonPath("$.data.scheduleWindows").isArray())
        .andExpect(jsonPath("$.data.availableActions").isArray());

    String upsertResp =
        mockMvc
            .perform(
                put("/api/admin/ad-slot-schedules")
                    .header("X-Admin-Token", "test-admin-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "slotCode":"HOME_TOP_CAROUSEL_TS",
                          "slotName":"唐山站首页焦点位",
                          "slotType":"HOME_TOP_BANNER",
                          "cityCode":"TS",
                          "cityName":"唐山",
                          "scheduleStatus":"ACTIVE",
                          "scheduleFillStatus":"PARTIAL",
                          "startDate":"2026-05-01",
                          "endDate":"2026-05-31",
                          "totalSlots":"2",
                          "soldSlots":"1",
                          "pricePerDay":"¥9800/天",
                          "creativeUrl":"https://cdn.huodaizi.com/ad/new-home-top-banner.png",
                          "advertiserName":"唐山钢贸联盟",
                          "campaignName":"5月集中采购节",
                          "operator":"admn12-admin"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.adSlotCode").value("HOME_TOP_CAROUSEL_TS"))
            .andExpect(jsonPath("$.data.slotType").value("HOME_TOP_BANNER"))
            .andExpect(jsonPath("$.data.scheduleStatus").value("ACTIVE"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> upsertBody = mapper.readValue(upsertResp, java.util.Map.class);
    java.util.Map<?, ?> upsertData = (java.util.Map<?, ?>) upsertBody.get("data");
    String createdScheduleId = String.valueOf(upsertData.get("scheduleId"));

    mockMvc
        .perform(
            put("/api/admin/ad-slot-schedules")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "slotCode":"HOME_TOP_CAROUSEL_TS",
                      "slotName":"唐山站首页焦点位",
                      "slotType":"HOME_TOP_BANNER",
                      "cityCode":"TS",
                      "cityName":"唐山",
                      "scheduleStatus":"PAUSED",
                      "scheduleFillStatus":"OFFLINE",
                      "startDate":"2026-05-01",
                      "endDate":"2026-05-31",
                      "totalSlots":"2",
                      "soldSlots":"1",
                      "pricePerDay":"¥9600/天",
                      "creativeUrl":"https://cdn.huodaizi.com/ad/new-home-top-banner-v2.png",
                      "advertiserName":"唐山钢贸联盟",
                      "campaignName":"5月集中采购节（暂停）",
                      "operator":"admn12-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.scheduleId").value(createdScheduleId))
        .andExpect(jsonPath("$.data.scheduleStatus").value("PAUSED"))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            get("/api/admin/ad-slot-schedules")
                .header("X-Admin-Token", "test-admin-token")
                .param("keyword", "唐山站首页焦点位")
                .param("slotType", "HOME_TOP_BANNER")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)))
        .andExpect(jsonPath("$.data.records[0].slotType").value("HOME_TOP_BANNER"));
  }
}
