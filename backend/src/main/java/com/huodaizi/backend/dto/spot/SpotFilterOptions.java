package com.huodaizi.backend.dto.spot;

import java.util.List;

public record SpotFilterOptions(
    List<String> categories,
    List<String> specs,
    List<String> cities,
    List<String> prices) {

  public static SpotFilterOptions defaultOptions() {
    return new SpotFilterOptions(
        List.of("全部品类", "螺纹钢", "热卷", "中厚板", "型钢", "管材"),
        List.of("全部规格", "HRB400E", "Q235B", "Q355B", "10-25mm", "16-40mm"),
        List.of("全部城市", "唐山", "天津", "无锡", "佛山", "武汉"),
        List.of("全部价格", "3000以下", "3000-3500", "3500-4000", "4000以上"));
  }
}
