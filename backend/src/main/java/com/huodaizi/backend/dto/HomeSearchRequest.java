package com.huodaizi.backend.dto;

import jakarta.validation.constraints.Size;

public record HomeSearchRequest(
    @Size(max = 32, message = "city length must be <= 32") String city,
    @Size(max = 32, message = "type length must be <= 32") String type,
    @Size(max = 64, message = "keyword length must be <= 64") String keyword) {}
