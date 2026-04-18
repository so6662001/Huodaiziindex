package com.huodaizi.backend.dto.freight;

import java.util.List;

public record FreightFilterOptions(
    List<String> origins,
    List<String> destinations,
    List<String> vehicleTypes,
    List<String> timeliness,
    List<String> returnTruck) {

  public static FreightFilterOptions defaultOptions() {
    return new FreightFilterOptions(
        List.of("全部起运地", "唐山", "天津", "武汉", "佛山", "无锡"),
        List.of("全部目的地", "无锡", "佛山", "广州", "南京", "长沙"),
        List.of("全部车型", "13米平板", "17.5米平板", "13米高栏", "厢式货车"),
        List.of("全部时效", "24小时内", "48小时内", "72小时内", "72小时以上"),
        List.of("不限", "有回程车", "无回程车"));
  }
}
