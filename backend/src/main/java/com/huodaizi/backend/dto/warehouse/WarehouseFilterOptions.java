package com.huodaizi.backend.dto.warehouse;

import java.util.List;

public record WarehouseFilterOptions(
    List<String> cities,
    List<String> warehouseTypes,
    List<String> categories,
    List<String> liftingCapabilities,
    List<String> prices) {

  public static WarehouseFilterOptions defaultOptions() {
    return new WarehouseFilterOptions(
        List.of("全部城市", "唐山", "天津", "无锡", "佛山", "武汉"),
        List.of("全部库型", "室内库", "露天场", "综合库"),
        List.of("全部品类", "螺纹钢", "热卷", "中厚板", "型钢", "管材"),
        List.of("全部能力", "10吨以下", "10-20吨", "20吨以上"),
        List.of("全部价格", "0.8元/吨/天以下", "0.8-1.0元/吨/天", "1.0元/吨/天以上"));
  }
}
