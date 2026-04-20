package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.Size;

public record N12InvoiceTitleSetDefaultRequest(
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
