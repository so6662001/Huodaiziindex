package com.huodaizi.backend.dto.auth;

public record N12InvoiceTitleDTO(
    String titleId,
    String titleType,
    String titleTypeText,
    String titleName,
    String taxNo,
    String bankName,
    String bankAccountNoMasked,
    String registeredAddress,
    String contactPhoneMasked,
    String email,
    boolean defaultTitle,
    String status,
    String statusText,
    String updatedAt) {}
