package com.huodaizi.backend.dto.sitead;

import jakarta.validation.constraints.Size;

public record SiteAdAdminCreateRequest(
    @Size(max = 120, message = "name 最大长度120")
    String name,
    @Size(max = 120, message = "price 最大长度120")
    String price,
    @Size(max = 1000, message = "desc 最大长度1000")
    String desc,
    @Size(max = 32, message = "optionType 最大长度32")
    String optionType,
    @Size(max = 1000, message = "optionValues 最大长度1000")
    String optionValues,
    @Size(max = 120, message = "city 最大长度120")
    String city,
    @Size(max = 120, message = "placement 最大长度120")
    String placement,
    @Size(max = 64, message = "duration 最大长度64")
    String duration,
    @Size(max = 200, message = "companyName 最大长度200")
    String companyName,
    @Size(max = 80, message = "contactName 最大长度80")
    String contactName,
    @Size(max = 32, message = "phone 最大长度32")
    String phone,
    @Size(max = 120, message = "budget 最大长度120")
    String budget,
    @Size(max = 1000, message = "remark 最大长度1000")
    String remark,
    @Size(max = 500, message = "link 最大长度500")
    String link,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    Boolean pinned) {}
