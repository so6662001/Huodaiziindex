package com.huodaizi.backend.dto.admn03;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record Admn03RolePermissionUpdateRequest(
    @NotEmpty(message = "permissionCodes 不能为空")
    List<
            @NotBlank(message = "permissionCode 不能为空")
            @Size(max = 64, message = "permissionCode 最大长度64")
            String>
        permissionCodes,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
