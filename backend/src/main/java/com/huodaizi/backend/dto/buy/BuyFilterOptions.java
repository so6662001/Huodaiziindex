package com.huodaizi.backend.dto.buy;

import java.util.List;

public record BuyFilterOptions(
    List<String> categories,
    List<String> specs,
    List<String> cities,
    List<String> arrivals) {

  public static BuyFilterOptions defaultOptions() {
    return new BuyFilterOptions(
        List.of("全部品类", "螺纹钢", "热卷", "中厚板", "型钢", "管材"),
        List.of("全部规格", "HRB400E", "Q235B", "Q355B", "10-25mm", "16-40mm"),
        List.of("全部城市", "郑州", "南京", "武汉", "佛山", "成都"),
        List.of("全部交期", "现货即提", "3天内到货", "7天内到货", "长期采购"));
  }
}
