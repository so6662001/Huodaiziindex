package com.huodaizi.backend.dto.storagedemand;

import java.util.List;

public record StorageDemandFilterOptions(
    List<String> cities,
    List<String> goodsCategories,
    List<String> serviceNeeds) {

  public static StorageDemandFilterOptions defaultOptions() {
    return new StorageDemandFilterOptions(
        List.of("全部城市", "唐山", "天津", "无锡", "佛山", "武汉", "成都"),
        List.of("全部品类", "螺纹钢", "热卷", "中厚板", "型钢", "管材", "其他"),
        List.of("全部服务", "仅仓储", "需要装卸", "需要分拣", "装卸+分拣"));
  }
}
