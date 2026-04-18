package com.huodaizi.backend.dto.transportdemand;

import java.util.List;

public record TransportDemandFilterOptions(
    List<String> origins,
    List<String> destinations,
    List<String> goodsCategories,
    List<String> vehicleTypes,
    List<String> timelinessOptions,
    List<String> invoiceNeeds) {

  public static TransportDemandFilterOptions defaultOptions() {
    return new TransportDemandFilterOptions(
        List.of("全部起运地", "唐山", "天津", "无锡", "佛山", "武汉", "广州", "长沙"),
        List.of("全部目的地", "唐山", "天津", "无锡", "佛山", "武汉", "广州", "长沙"),
        List.of("全部品类", "螺纹钢", "热卷", "中厚板", "型钢", "管材", "其他"),
        List.of("全部车型", "13米平板", "17.5米平板", "13米高栏", "厢式货车", "不限车型"),
        List.of("全部时效", "24小时内", "48小时内", "72小时内", "一周内"),
        List.of("全部票据", "需要开票", "无需开票"));
  }
}
