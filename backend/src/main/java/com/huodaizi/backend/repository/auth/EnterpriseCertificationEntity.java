package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class EnterpriseCertificationEntity {
  private final String certificationId;
  private final String userId;
  private final String account;
  private String status;
  private String companyName;
  private String unifiedSocialCreditCode;
  private String legalPersonName;
  private String legalPersonIdNo;
  private String contactName;
  private String contactMobile;
  private String contactMobileMasked;
  private String businessLicenseUrl;
  private String legalIdFrontUrl;
  private String legalIdBackUrl;
  private String bankAccountName;
  private String bankAccountNo;
  private String bankName;
  private String province;
  private String city;
  private String address;
  private String remark;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime submittedAt;
  private LocalDateTime updatedAt;

  public EnterpriseCertificationEntity(
      String certificationId,
      String userId,
      String account,
      String status,
      String companyName,
      String unifiedSocialCreditCode,
      String legalPersonName,
      String legalPersonIdNo,
      String contactName,
      String contactMobile,
      String contactMobileMasked,
      String businessLicenseUrl,
      String legalIdFrontUrl,
      String legalIdBackUrl,
      String bankAccountName,
      String bankAccountNo,
      String bankName,
      String province,
      String city,
      String address,
      String remark,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime submittedAt,
      LocalDateTime updatedAt) {
    this.certificationId = certificationId;
    this.userId = userId;
    this.account = account;
    this.status = status;
    this.companyName = companyName;
    this.unifiedSocialCreditCode = unifiedSocialCreditCode;
    this.legalPersonName = legalPersonName;
    this.legalPersonIdNo = legalPersonIdNo;
    this.contactName = contactName;
    this.contactMobile = contactMobile;
    this.contactMobileMasked = contactMobileMasked;
    this.businessLicenseUrl = businessLicenseUrl;
    this.legalIdFrontUrl = legalIdFrontUrl;
    this.legalIdBackUrl = legalIdBackUrl;
    this.bankAccountName = bankAccountName;
    this.bankAccountNo = bankAccountNo;
    this.bankName = bankName;
    this.province = province;
    this.city = city;
    this.address = address;
    this.remark = remark;
    this.operator = operator;
    this.createdAt = createdAt;
    this.submittedAt = submittedAt;
    this.updatedAt = updatedAt;
  }

  public String getCertificationId() {
    return certificationId;
  }

  public String getUserId() {
    return userId;
  }

  public String getAccount() {
    return account;
  }

  public String getStatus() {
    return status;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getUnifiedSocialCreditCode() {
    return unifiedSocialCreditCode;
  }

  public String getLegalPersonName() {
    return legalPersonName;
  }

  public String getLegalPersonIdNo() {
    return legalPersonIdNo;
  }

  public String getContactName() {
    return contactName;
  }

  public String getContactMobile() {
    return contactMobile;
  }

  public String getContactMobileMasked() {
    return contactMobileMasked;
  }

  public String getBusinessLicenseUrl() {
    return businessLicenseUrl;
  }

  public String getLegalIdFrontUrl() {
    return legalIdFrontUrl;
  }

  public String getLegalIdBackUrl() {
    return legalIdBackUrl;
  }

  public String getBankAccountName() {
    return bankAccountName;
  }

  public String getBankAccountNo() {
    return bankAccountNo;
  }

  public String getBankName() {
    return bankName;
  }

  public String getProvince() {
    return province;
  }

  public String getCity() {
    return city;
  }

  public String getAddress() {
    return address;
  }

  public String getRemark() {
    return remark;
  }

  public String getOperator() {
    return operator;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getSubmittedAt() {
    return submittedAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void refreshFromSubmission(
      String status,
      String companyName,
      String unifiedSocialCreditCode,
      String legalPersonName,
      String legalPersonIdNo,
      String contactName,
      String contactMobile,
      String contactMobileMasked,
      String businessLicenseUrl,
      String legalIdFrontUrl,
      String legalIdBackUrl,
      String bankAccountName,
      String bankAccountNo,
      String bankName,
      String province,
      String city,
      String address,
      String remark,
      String operator,
      LocalDateTime now) {
    this.status = status;
    this.companyName = companyName;
    this.unifiedSocialCreditCode = unifiedSocialCreditCode;
    this.legalPersonName = legalPersonName;
    this.legalPersonIdNo = legalPersonIdNo;
    this.contactName = contactName;
    this.contactMobile = contactMobile;
    this.contactMobileMasked = contactMobileMasked;
    this.businessLicenseUrl = businessLicenseUrl;
    this.legalIdFrontUrl = legalIdFrontUrl;
    this.legalIdBackUrl = legalIdBackUrl;
    this.bankAccountName = bankAccountName;
    this.bankAccountNo = bankAccountNo;
    this.bankName = bankName;
    this.province = province;
    this.city = city;
    this.address = address;
    this.remark = remark;
    this.operator = operator;
    this.submittedAt = now;
    this.updatedAt = now;
  }
}
