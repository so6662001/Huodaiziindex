package com.huodaizi.backend.dto.admn03;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record Admn03RoleUpsertRequest(
    @NotBlank(message = "roleCode 不能为空")
    @Size(max = 32, message = "roleCode 最大长度32")
    String roleCode,
    @NotBlank(message = "roleName 不能为空")
    @Size(max = 64, message = "roleName 最大长度64")
    String roleName,
    @Size(max = 300, message = "description 最大长度300")
    String description,
    @NotEmpty(message = "permissionCodes 不能为空")
    List<@NotBlank(message = "permissionCode 不能为空") @Size(max = 64, message = "permissionCode 最大长度64")
            String>
        permissionCodes,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
