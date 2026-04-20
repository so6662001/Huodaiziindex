package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class N06OrderEntity {
  private final String orderId;
  private final String orderNo;
  private final String userId;
  private final String account;
  private final String inquiryNo;
  private final String goodsName;
  private final String quantityTon;
  private final String deliveryCity;
  private final String supplierName;
  private final String buyerCompany;
  private final String dealUnitPrice;
  private final String dealTotalAmount;
  private String orderStatus;
  private String orderStatusText;
  private String contractStatus;
  private String contractStatusText;
  private String nextAction;
  private String expectedDeliveryAt;
  private String invoiceStatus;
  private String invoiceNo;
  private String paymentStatus;
  private String paymentAt;
  private String latestRemark;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<OrderTimelineNode> timeline;

  public N06OrderEntity(
      String orderId,
      String orderNo,
      String userId,
      String account,
      String inquiryNo,
      String goodsName,
      String quantityTon,
      String deliveryCity,
      String supplierName,
      String buyerCompany,
      String dealUnitPrice,
      String dealTotalAmount,
      String orderStatus,
      String orderStatusText,
      String contractStatus,
      String contractStatusText,
      String nextAction,
      String expectedDeliveryAt,
      String invoiceStatus,
      String invoiceNo,
      String paymentStatus,
      String paymentAt,
      String latestRemark,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<OrderTimelineNode> timeline) {
    this.orderId = orderId;
    this.orderNo = orderNo;
    this.userId = userId;
    this.account = account;
    this.inquiryNo = inquiryNo;
    this.goodsName = goodsName;
    this.quantityTon = quantityTon;
    this.deliveryCity = deliveryCity;
    this.supplierName = supplierName;
    this.buyerCompany = buyerCompany;
    this.dealUnitPrice = dealUnitPrice;
    this.dealTotalAmount = dealTotalAmount;
    this.orderStatus = orderStatus;
    this.orderStatusText = orderStatusText;
    this.contractStatus = contractStatus;
    this.contractStatusText = contractStatusText;
    this.nextAction = nextAction;
    this.expectedDeliveryAt = expectedDeliveryAt;
    this.invoiceStatus = invoiceStatus;
    this.invoiceNo = invoiceNo;
    this.paymentStatus = paymentStatus;
    this.paymentAt = paymentAt;
    this.latestRemark = latestRemark;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.timeline = timeline == null ? new ArrayList<>() : new ArrayList<>(timeline);
  }

  public String getOrderId() {
    return orderId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public String getUserId() {
    return userId;
  }

  public String getAccount() {
    return account;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getGoodsName() {
    return goodsName;
  }

  public String getQuantityTon() {
    return quantityTon;
  }

  public String getDeliveryCity() {
    return deliveryCity;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public String getBuyerCompany() {
    return buyerCompany;
  }

  public String getDealUnitPrice() {
    return dealUnitPrice;
  }

  public String getDealTotalAmount() {
    return dealTotalAmount;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public String getOrderStatusText() {
    return orderStatusText;
  }

  public String getContractStatus() {
    return contractStatus;
  }

  public String getContractStatusText() {
    return contractStatusText;
  }

  public String getNextAction() {
    return nextAction;
  }

  public String getExpectedDeliveryAt() {
    return expectedDeliveryAt;
  }

  public String getInvoiceStatus() {
    return invoiceStatus;
  }

  public String getInvoiceNo() {
    return invoiceNo;
  }

  public String getPaymentStatus() {
    return paymentStatus;
  }

  public String getPaymentAt() {
    return paymentAt;
  }

  public String getLatestRemark() {
    return latestRemark;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public List<OrderTimelineNode> getTimeline() {
    return List.copyOf(timeline);
  }

  public void updateStatus(String status, String operator, LocalDateTime now, String remark) {
    this.orderStatus = status;
    this.orderStatusText = statusText(status);
    this.updatedAt = now;
    this.latestRemark = remark == null ? "" : remark;

    switch (status) {
      case "PENDING_SIGN" -> {
        contractStatus = "CONTRACT_PENDING";
        contractStatusText = "待签署";
        nextAction = "上传合同并完成双方签章";
      }
      case "SIGNED" -> {
        contractStatus = "SIGNED";
        contractStatusText = "已签署";
        nextAction = "安排提货车辆并确认仓库窗口";
      }
      case "PICKUP_IN_PROGRESS" -> {
        contractStatus = "SIGNED";
        contractStatusText = "已签署";
        nextAction = "跟进在途提货并回传签收信息";
      }
      case "RECONCILING" -> {
        paymentStatus = "PARTIAL_PAID";
        paymentAt = now.toString();
        nextAction = "核对对账单并完成尾款回款";
      }
      case "COMPLETED" -> {
        paymentStatus = "PAID";
        paymentAt = now.toString();
        nextAction = "订单已完结，可发起复购";
      }
      case "CANCELLED" -> {
        contractStatus = "CANCELLED";
        contractStatusText = "已取消";
        nextAction = "如需恢复，请重新发起订单流程";
      }
      default -> {
      }
    }

    timeline.add(
        new OrderTimelineNode(
            "STATUS_" + status,
            "订单状态更新",
            "已完成",
            status,
            "操作人：" + (operator == null || operator.isBlank() ? "system" : operator),
            remark == null || remark.isBlank() ? "无备注" : remark,
            now.toString()));
  }

  private String statusText(String status) {
    return switch (status) {
      case "PENDING_SIGN" -> "待签署";
      case "SIGNED" -> "已签署";
      case "PICKUP_IN_PROGRESS" -> "提货中";
      case "RECONCILING" -> "对账中";
      case "COMPLETED" -> "已完成";
      case "CANCELLED" -> "已取消";
      default -> "处理中";
    };
  }

  public static final class OrderTimelineNode {
    private final String nodeCode;
    private final String nodeName;
    private final String statusText;
    private final String status;
    private final String owner;
    private final String remark;
    private final String happenedAt;

    public OrderTimelineNode(
        String nodeCode,
        String nodeName,
        String statusText,
        String status,
        String owner,
        String remark,
        String happenedAt) {
      this.nodeCode = nodeCode;
      this.nodeName = nodeName;
      this.statusText = statusText;
      this.status = status;
      this.owner = owner;
      this.remark = remark;
      this.happenedAt = happenedAt;
    }

    public String getNodeCode() {
      return nodeCode;
    }

    public String getNodeName() {
      return nodeName;
    }

    public String getStatusText() {
      return statusText;
    }

    public String getStatus() {
      return status;
    }

    public String getOwner() {
      return owner;
    }

    public String getRemark() {
      return remark;
    }

    public String getHappenedAt() {
      return happenedAt;
    }
  }
}
