package com.huodaizi.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "huodaizi.admin.auth")
public class AdminAuthProperties {

  /**
   * Enable/disable admin token auth quickly in local environments.
   */
  private boolean enabled = true;

  /**
   * Simple MVP static token. Should be replaced with JWT/RBAC in production.
   */
  private String token = "change-me-admin-token";

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
