package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record N05NegotiationSendMessageRequest(
    @NotBlank(message = "senderRole 不能为空")
    @Pattern(regexp = "^(BUYER|SUPPLIER)$", message = "senderRole 仅支持 BUYER/SUPPLIER")
    String senderRole,
    @NotBlank(message = "messageType 不能为空")
    @Pattern(regexp = "^(TEXT|OFFER|SYSTEM)$", message = "messageType 仅支持 TEXT/OFFER/SYSTEM")
    String messageType,
    @NotBlank(message = "content 不能为空")
    @Size(max = 500, message = "content 最大长度500")
    String content,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
