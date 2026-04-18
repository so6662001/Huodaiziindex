package com.huodaizi.backend.repository.logistics;

public enum LogisticsSectionType {
  QUICK_ENTRY("QK", "快捷入口"),
  WAREHOUSE("WH", "推荐仓库"),
  FREIGHT("FR", "推荐车队专线"),
  STORAGE_DEMAND("SD", "最新仓储需求"),
  TRANSPORT_DEMAND("TD", "最新运输需求"),
  CITY_STATION("CT", "热门城市入口"),
  AD_SLOT("AD", "仓储物流招商专区");

  private final String prefix;
  private final String sectionName;

  LogisticsSectionType(String prefix, String sectionName) {
    this.prefix = prefix;
    this.sectionName = sectionName;
  }

  public String prefix() {
    return prefix;
  }

  public String sectionName() {
    return sectionName;
  }
}
