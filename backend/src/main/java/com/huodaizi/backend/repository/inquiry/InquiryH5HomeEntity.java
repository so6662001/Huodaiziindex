package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;
import java.util.List;

public class InquiryH5HomeEntity {
  private final String city;
  private final String weather;
  private final String heroTitle;
  private final String heroSubtitle;
  private final List<QuickNavEntity> quickNavs;
  private final List<BannerEntity> banners;
  private final List<MarketCardEntity> marketCards;
  private final List<RecommendationEntity> recommendations;
  private final LocalDateTime updatedAt;

  public InquiryH5HomeEntity(
      String city,
      String weather,
      String heroTitle,
      String heroSubtitle,
      List<QuickNavEntity> quickNavs,
      List<BannerEntity> banners,
      List<MarketCardEntity> marketCards,
      List<RecommendationEntity> recommendations,
      LocalDateTime updatedAt) {
    this.city = city;
    this.weather = weather;
    this.heroTitle = heroTitle;
    this.heroSubtitle = heroSubtitle;
    this.quickNavs = quickNavs;
    this.banners = banners;
    this.marketCards = marketCards;
    this.recommendations = recommendations;
    this.updatedAt = updatedAt;
  }

  public String getCity() {
    return city;
  }

  public String getWeather() {
    return weather;
  }

  public String getHeroTitle() {
    return heroTitle;
  }

  public String getHeroSubtitle() {
    return heroSubtitle;
  }

  public List<QuickNavEntity> getQuickNavs() {
    return quickNavs;
  }

  public List<BannerEntity> getBanners() {
    return banners;
  }

  public List<MarketCardEntity> getMarketCards() {
    return marketCards;
  }

  public List<RecommendationEntity> getRecommendations() {
    return recommendations;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public record QuickNavEntity(
      String code,
      String title,
      String subtitle,
      String icon,
      String actionUrl,
      String badge) {}

  public record BannerEntity(String bannerId, String title, String desc, String actionUrl, String colorTag) {}

  public record MarketCardEntity(
      String commodity,
      String spec,
      String city,
      String latestPrice,
      String trend,
      String volume) {}

  public record RecommendationEntity(
      String merchantId,
      String merchantName,
      String score,
      String tags,
      String responseMinutes,
      String actionUrl) {}
}
