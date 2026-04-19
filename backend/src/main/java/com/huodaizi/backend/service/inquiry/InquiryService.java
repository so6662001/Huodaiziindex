package com.huodaizi.backend.service.inquiry;

import com.huodaizi.backend.dto.inquiry.InquiryCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryStatus;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InquiryService {

  private final InMemoryInquiryRepository repository;

  public InquiryService(InMemoryInquiryRepository repository) {
    this.repository = repository;
  }

  public InquiryCreateResponse create(InquiryCreateRequest request) {
    InquiryEntity entity = repository.create(request);
    return new InquiryCreateResponse(
        entity.getId(),
        entity.getInquiryNo(),
        entity.getStatus().name(),
        "询价已提交，正在为您匹配优质商家");
  }

  public InquiryListResponse list(InquiryListRequest request) {
    List<InquiryEntity> filtered = repository.listMine(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<InquiryEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    return new InquiryListResponse(
        paged.stream().map(this::toItem).toList(),
        filtered.size(),
        page,
        pageSize,
        countByStatus(filtered, InquiryStatus.OPEN),
        countByStatus(filtered, InquiryStatus.QUOTING),
        countByStatus(filtered, InquiryStatus.DEAL_DONE),
        countByStatus(filtered, InquiryStatus.CLOSED));
  }

  private InquiryItemDTO toItem(InquiryEntity entity) {
    return new InquiryItemDTO(
        entity.getId(),
        entity.getInquiryNo(),
        entity.getCategoryCode(),
        entity.getSpecText(),
        entity.getDemandQtyTon(),
        entity.getDeliveryCity(),
        entity.getExpectedDeliveryAt(),
        entity.getInvoiceNeed(),
        entity.getStatus().name(),
        entity.getQuoteSupplierCount(),
        entity.getCreatedAt().toString(),
        entity.getUpdatedAt().toString());
  }

  private int countByStatus(List<InquiryEntity> items, InquiryStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }
}

