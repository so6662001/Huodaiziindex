package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class Admn05CategorySpecDictEntity {
  private final String dictId;
  private String categoryCode;
  private String categoryName;
  private String specName;
  private String specValue;
  private String sceneCode;
  private String status;
  private int sortNo;
  private String remark;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Admn05CategorySpecDictEntity(
      String dictId,
      String categoryCode,
      String categoryName,
      String specName,
      String specValue,
      String sceneCode,
      String status,
      int sortNo,
      String remark,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.dictId = dictId;
    this.categoryCode = categoryCode;
    this.categoryName = categoryName;
    this.specName = specName;
    this.specValue = specValue;
    this.sceneCode = sceneCode;
    this.status = status;
    this.sortNo = sortNo;
    this.remark = remark;
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getDictId() {
    return dictId;
  }

  public String getCategoryCode() {
    return categoryCode;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getSpecName() {
    return specName;
  }

  public String getSpecValue() {
    return specValue;
  }

  public String getSceneCode() {
    return sceneCode;
  }

  public String getStatus() {
    return status;
  }

  public int getSortNo() {
    return sortNo;
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

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void update(
      String newCategoryCode,
      String newCategoryName,
      String newSpecName,
      String newSpecValue,
      String newSceneCode,
      String newStatus,
      int newSortNo,
      String newRemark,
      String newOperator,
      LocalDateTime now) {
    this.categoryCode = newCategoryCode;
    this.categoryName = newCategoryName;
    this.specName = newSpecName;
    this.specValue = newSpecValue;
    this.sceneCode = newSceneCode;
    this.status = newStatus;
    this.sortNo = newSortNo;
    this.remark = newRemark;
    this.operator = newOperator;
    this.updatedAt = now;
  }
}
