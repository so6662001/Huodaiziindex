package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N11MessageSettingsResponse(
    String settingId,
    String userId,
    String contactMobileMasked,
    boolean globalPushEnabled,
    boolean appPushEnabled,
    boolean smsPushEnabled,
    boolean marketingEnabled,
    boolean transactionEnabled,
    boolean riskEnabled,
    boolean doNotDisturbEnabled,
    String doNotDisturbStart,
    String doNotDisturbEnd,
    String latestRemark,
    String channel,
    String updatedBy,
    List<String> availableActions,
    String tipText,
    String updatedAt) {}
