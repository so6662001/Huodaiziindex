package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.List;

public class AuthUserEntity {
  private final String userId;
  private final String accountType;
  private final String account;
  private final String phone;
  private final String phoneMasked;
  private final String passwordHash;
  private final String companyName;
  private final String contactName;
  private final String role;
  private final String status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public AuthUserEntity(
      String userId,
      String accountType,
      String account,
      String phone,
      String phoneMasked,
      String passwordHash,
      String companyName,
      String contactName,
      String role,
      String status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.userId = userId;
    this.accountType = accountType;
    this.account = account;
    this.phone = phone;
    this.phoneMasked = phoneMasked;
    this.passwordHash = passwordHash;
    this.companyName = companyName;
    this.contactName = contactName;
    this.role = role;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getAccountType() {
    return accountType;
  }

  public String getAccount() {
    return account;
  }

  public String getUserId() {
    return userId;
  }

  public String getPhone() {
    return phone;
  }

  public String getPhoneMasked() {
    return phoneMasked;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getContactName() {
    return contactName;
  }

  public String getRole() {
    return role;
  }

  public List<String> getIdentityCodes() {
    return List.of("BUYER", "SUPPLIER", "OPERATOR");
  }

  public String getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void touch(LocalDateTime now) {
    this.updatedAt = now;
  }
}
