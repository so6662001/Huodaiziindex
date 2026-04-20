package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admn09ArbitrationTicketEntity {
  private final String ticketId;
  private final String disputeId;
  private final String city;
  private String arbitrationStatus;
  private String priorityLevel;
  private String assignedArbitrator;
  private String hearingAt;
  private String latestConclusion;
  private String latestRemark;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<TimelineNode> timelineNodes;

  public Admn09ArbitrationTicketEntity(
      String ticketId,
      String disputeId,
      String city,
      String arbitrationStatus,
      String priorityLevel,
      String assignedArbitrator,
      String hearingAt,
      String latestConclusion,
      String latestRemark,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.ticketId = ticketId;
    this.disputeId = disputeId;
    this.city = city;
    this.arbitrationStatus = arbitrationStatus;
    this.priorityLevel = priorityLevel;
    this.assignedArbitrator = assignedArbitrator;
    this.hearingAt = hearingAt;
    this.latestConclusion = latestConclusion;
    this.latestRemark = latestRemark;
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.timelineNodes = new ArrayList<>();
  }

  public String getTicketId() {
    return ticketId;
  }

  public String getDisputeId() {
    return disputeId;
  }

  public String getCity() {
    return city;
  }

  public String getArbitrationStatus() {
    return arbitrationStatus;
  }

  public String getPriorityLevel() {
    return priorityLevel;
  }

  public String getAssignedArbitrator() {
    return assignedArbitrator;
  }

  public String getHearingAt() {
    return hearingAt;
  }

  public String getLatestConclusion() {
    return latestConclusion;
  }

  public String getLatestRemark() {
    return latestRemark;
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

  public List<TimelineNode> getTimelineNodes() {
    return List.copyOf(timelineNodes);
  }

  public void updateCase(
      String newArbitrationStatus,
      String newPriorityLevel,
      String newAssignedArbitrator,
      String newHearingAt,
      String newLatestConclusion,
      String newLatestRemark,
      String newOperator,
      LocalDateTime now) {
    this.arbitrationStatus = newArbitrationStatus;
    this.priorityLevel = newPriorityLevel;
    this.assignedArbitrator = newAssignedArbitrator;
    this.hearingAt = newHearingAt;
    this.latestConclusion = newLatestConclusion;
    this.latestRemark = newLatestRemark;
    this.operator = newOperator;
    this.updatedAt = now;
  }

  public void appendTimeline(
      String nodeCode,
      String nodeName,
      String status,
      String statusText,
      String handler,
      String remark,
      String happenedAt) {
    timelineNodes.add(new TimelineNode(nodeCode, nodeName, status, statusText, handler, remark, happenedAt));
  }

  public static final class TimelineNode {
    private final String nodeCode;
    private final String nodeName;
    private final String status;
    private final String statusText;
    private final String handler;
    private final String remark;
    private final String happenedAt;

    public TimelineNode(
        String nodeCode,
        String nodeName,
        String status,
        String statusText,
        String handler,
        String remark,
        String happenedAt) {
      this.nodeCode = nodeCode;
      this.nodeName = nodeName;
      this.status = status;
      this.statusText = statusText;
      this.handler = handler;
      this.remark = remark;
      this.happenedAt = happenedAt;
    }

    public String getNodeCode() {
      return nodeCode;
    }

    public String getNodeName() {
      return nodeName;
    }

    public String getStatus() {
      return status;
    }

    public String getStatusText() {
      return statusText;
    }

    public String getHandler() {
      return handler;
    }

    public String getRemark() {
      return remark;
    }

    public String getHappenedAt() {
      return happenedAt;
    }
  }
}
