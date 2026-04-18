package com.huodaizi.backend.dto.freightdetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FreightDetailAdminCreateRequest(
    @NotBlank(message = "title 不能为空")
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @Size(max = 120, message = "provider 最大长度120")
    String provider,
    @Size(max = 120, message = "route 最大长度120")
    String route,
    @Size(max = 80, message = "vehicle 最大长度80")
    String vehicle,
    @Size(max = 80, message = "loadRange 最大长度80")
    String loadRange,
    @Size(max = 80, message = "frequency 最大长度80")
    String frequency,
    @Size(max = 80, message = "timeliness 最大长度80")
    String timeliness,
    @Size(max = 80, message = "quote 最大长度80")
    String quote,
    @Size(max = 600, message = "serviceTags 最大长度600")
    String serviceTags,
    @Size(max = 1000, message = "description 最大长度1000")
    String description,
    @Size(max = 120, message = "contactName 最大长度120")
    String contactName,
    @Size(max = 32, message = "contactPhone 最大长度32")
    String contactPhone,
    @Size(max = 120, message = "serviceStatus 最大长度120")
    String serviceStatus,
    @Size(max = 500, message = "content 最大长度500")
    String content,
    @Size(max = 120, message = "relatedId 最大长度120")
    String relatedId,
    @Size(max = 120, message = "link 最大长度120")
    String link,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    Boolean pinned) {}
