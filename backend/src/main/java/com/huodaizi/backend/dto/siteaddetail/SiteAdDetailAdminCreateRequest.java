package com.huodaizi.backend.dto.siteaddetail;

import jakarta.validation.constraints.Size;

public record SiteAdDetailAdminCreateRequest(
    @Size(max = 120, message = "placementId 最大长度120")
    String placementId,
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @Size(max = 1000, message = "desc 最大长度1000")
    String desc,
    @Size(max = 120, message = "price 最大长度120")
    String price,
    @Size(max = 120, message = "defaultCity 最大长度120")
    String defaultCity,
    @Size(max = 64, message = "defaultDuration 最大长度64")
    String defaultDuration,
    @Size(max = 120, message = "exposureScene 最大长度120")
    String exposureScene,
    @Size(max = 120, message = "recommendedCategory 最大长度120")
    String recommendedCategory,
    @Size(max = 200, message = "reachHint 最大长度200")
    String reachHint,
    @Size(max = 500, message = "link 最大长度500")
    String link,
    @Size(max = 200, message = "companyName 最大长度200")
    String companyName,
    @Size(max = 120, message = "contactName 最大长度120")
    String contactName,
    @Size(max = 32, message = "contactPhone 最大长度32")
    String contactPhone,
    @Size(max = 120, message = "city 最大长度120")
    String city,
    @Size(max = 64, message = "duration 最大长度64")
    String duration,
    @Size(max = 120, message = "budget 最大长度120")
    String budget,
    @Size(max = 1000, message = "remark 最大长度1000")
    String remark,
    @Size(max = 200, message = "companyName 最大长度200")
    String metricKey,
    @Size(max = 500, message = "content 最大长度500")
    String content,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    Boolean pinned) {}
