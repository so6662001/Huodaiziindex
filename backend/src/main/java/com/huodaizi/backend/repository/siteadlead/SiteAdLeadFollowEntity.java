package com.huodaizi.backend.repository.siteadlead;

import java.time.LocalDateTime;

public class SiteAdLeadFollowEntity {
  private final String id;
  private final String leadId;
  private final String operator;
  private final String action;
  private final String content;
  private final String nextAction;
  private final LocalDateTime createdAt;

  public SiteAdLeadFollowEntity(
      String id,
      String leadId,
      String operator,
      String action,
      String content,
      String nextAction,
      LocalDateTime createdAt) {
    this.id = id;
    this.leadId = leadId;
    this.operator = operator;
    this.action = action;
    this.content = content;
    this.nextAction = nextAction;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public String getLeadId() {
    return leadId;
  }

  public String getOperator() {
    return operator;
  }

  public String getAction() {
    return action;
  }

  public String getContent() {
    return content;
  }

  public String getNextAction() {
    return nextAction;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
