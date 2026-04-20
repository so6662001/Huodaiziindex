package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class N10CashierOrderEntity {
  private final String cashierId;
  private final String userId;
  private final String orderId;
  private final String orderNo;
  private final String inquiryNo;
  private final String buyerCompany;
  private final String supplierName;
  private final String goodsName;
  private final String amountPayable;
  private String amountPaid;
  private String amountOutstanding;
  private final String currency;
  private String payStatus;
  private String payStatusText;
  private String payChannel;
  private String payChannelText;
  private String latestRemark;
  private String paidAt;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<PayTimelineNode> timeline;

  public N10CashierOrderEntity(
      String cashierId,
      String userId,
      String orderId,
      String orderNo,
      String inquiryNo,
      String buyerCompany,
      String supplierName,
      String goodsName,
      String amountPayable,
      String amountPaid,
      String amountOutstanding,
      String currency,
      String payStatus,
      String payStatusText,
      String payChannel,
      String payChannelText,
      String latestRemark,
      String paidAt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<PayTimelineNode> timeline) {
    this.cashierId = cashierId;
    this.userId = userId;
    this.orderId = orderId;
    this.orderNo = orderNo;
    this.inquiryNo = inquiryNo;
    this.buyerCompany = buyerCompany;
    this.supplierName = supplierName;
    this.goodsName = goodsName;
    this.amountPayable = amountPayable;
    this.amountPaid = amountPaid;
    this.amountOutstanding = amountOutstanding;
    this.currency = currency;
    this.payStatus = payStatus;
    this.payStatusText = payStatusText;
    this.payChannel = payChannel;
    this.payChannelText = payChannelText;
    this.latestRemark = latestRemark;
    this.paidAt = paidAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.timeline = timeline == null ? new ArrayList<>() : new ArrayList<>(timeline);
  }

  public String getCashierId() {
    return cashierId;
  }

  public String getUserId() {
    return userId;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getBuyerCompany() {
    return buyerCompany;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public String getGoodsName() {
    return goodsName;
  }

  public String getAmountPayable() {
    return amountPayable;
  }

  public String getAmountPaid() {
    return amountPaid;
  }

  public String getAmountOutstanding() {
    return amountOutstanding;
  }

  public String getCurrency() {
    return currency;
  }

  public String getPayStatus() {
    return payStatus;
  }

  public String getPayStatusText() {
    return payStatusText;
  }

  public String getPayChannel() {
    return payChannel;
  }

  public String getPayChannelText() {
    return payChannelText;
  }

  public String getLatestRemark() {
    return latestRemark;
  }

  public String getPaidAt() {
    return paidAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public List<PayTimelineNode> getTimeline() {
    return List.copyOf(timeline);
  }

  public void markPaid(
      String payChannel,
      String payChannelText,
      String amountPaid,
      String amountOutstanding,
      String remark,
      LocalDateTime now,
      String operator) {
    this.payStatus = "PAID";
    this.payStatusText = "已支付";
    this.payChannel = payChannel;
    this.payChannelText = payChannelText;
    this.amountPaid = amountPaid;
    this.amountOutstanding = amountOutstanding;
    this.latestRemark = remark;
    this.paidAt = now == null ? null : now.toString();
    this.updatedAt = now;
    this.timeline.add(
        new PayTimelineNode(
            "PAY_SUCCESS",
            "支付成功",
            "PAID",
            "已支付",
            operator == null || operator.isBlank() ? "system" : operator,
            remark,
            now == null ? null : now.toString()));
  }

  public static final class PayTimelineNode {
    private final String nodeCode;
    private final String nodeName;
    private final String status;
    private final String statusText;
    private final String handler;
    private final String remark;
    private final String happenedAt;

    public PayTimelineNode(
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
