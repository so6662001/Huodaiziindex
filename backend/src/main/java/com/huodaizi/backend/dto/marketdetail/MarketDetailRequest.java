package com.huodaizi.backend.dto.marketdetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarketDetailRequest(
    @NotBlank(message = "symbol 不能为空")
    @Size(max = 32, message = "symbol 最大长度32")
    String symbol,
    @NotBlank(message = "city 不能为空")
    @Size(max = 32, message = "city 最大长度32")
    String city) {}
