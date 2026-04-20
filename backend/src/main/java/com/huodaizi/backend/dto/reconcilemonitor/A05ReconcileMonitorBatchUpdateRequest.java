package com.huodaizi.backend.dto.reconcilemonitor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record A05ReconcileMonitorBatchUpdateRequest(
    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile,
    @NotEmpty(message = "reconcileIds 不能为空")
    List<
            @NotBlank(message = "reconcileId 不能为空")
            @Size(max = 64, message = "reconcileId 最大长度64")
            String>
        reconcileIds,
    @NotBlank(message = "status 不能为空")
    @Size(max = 32, message = "status 最大长度32")
    String status,
    @Size(max = 32, message = "paidAmount 最大长度32")
    String paidAmount,
    @Size(max = 32, message = "operator 最大长度32")
    String operator,
    @Size(max = 300, message = "remark 最大长度300")
    String remark) {}
