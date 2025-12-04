package com.kursaddcinar.minierp.service.impl;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.entity.*;
import com.kursaddcinar.minierp.exception.BusinessRuleException;
import com.kursaddcinar.minierp.exception.ResourceNotFoundException;
import com.kursaddcinar.minierp.repository.*;
import com.kursaddcinar.minierp.service.ICariAccountService;
import com.kursaddcinar.minierp.service.ISalesInvoiceService;
import com.kursaddcinar.minierp.service.IStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesInvoiceServiceImpl implements ISalesInvoiceService {

    private final SalesInvoiceRepository invoiceRepository;
    private final SalesInvoiceDetailRepository detailRepository;
    private final CariAccountRepository cariRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    
    // Stok ve Cari güncellemeleri için diğer servisleri çağıracağız
    private final IStockService stockService;
    private final ICariAccountService cariAccountService;

    @Override
    public ApiResponse<Page<DtoSalesInvoice>> getInvoices(Pageable pageable, String status, LocalDateTime startDate, LocalDateTime endDate, Integer cariId) {
        // Filtreleme mantığı için Specification kullanılabilir ama şimdilik findAll dönüyorum
        // İhtiyaca göre Repository'deki filtre metodları buraya bağlanır
        Page<SalesInvoice> page = invoiceRepository.findAll(pageable);
        return ApiResponse.success(page.map(this::mapToDto));
    }

    @Override
    public ApiResponse<DtoSalesInvoice> getInvoiceById(Integer id) {
        SalesInvoice invoice = invoiceRepository.findByInvoiceId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura bulunamadı: " + id));
        return ApiResponse.success(mapToDto(invoice));
    }

    @Override
    public ApiResponse<DtoSalesInvoice> getInvoiceByNo(String invoiceNo) {
        SalesInvoice invoice = invoiceRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura bulunamadı: " + invoiceNo));
        return ApiResponse.success(mapToDto(invoice));
    }

    @Override
    @Transactional
    public ApiResponse<DtoSalesInvoice> createInvoice(DtoCreateSalesInvoice createDto) {
        if (invoiceRepository.existsByInvoiceNo(createDto.getInvoiceNo())) {
            throw new BusinessRuleException("Bu fatura numarası zaten kullanılıyor.");
        }

        CariAccount cari = cariRepository.findById(createDto.getCariId())
                .orElseThrow(() -> new ResourceNotFoundException("Cari bulunamadı."));
        Warehouse warehouse = warehouseRepository.findById(createDto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Depo bulunamadı."));

        SalesInvoice invoice = new SalesInvoice();
        invoice.setInvoiceNo(createDto.getInvoiceNo());
        invoice.setCariAccount(cari);
        invoice.setWarehouse(warehouse);
        invoice.setInvoiceDate(createDto.getInvoiceDate());
        invoice.setDueDate(createDto.getDueDate());
        invoice.setDescription(createDto.getDescription());
        invoice.setStatus("DRAFT"); // İlk kayıt her zaman taslak

        // Detayları işle ve toplamları hesapla
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal vatTotal = BigDecimal.ZERO;
        BigDecimal netTotal = BigDecimal.ZERO;

        List<SalesInvoiceDetail> details = new ArrayList<>();
        for (DtoCreateSalesInvoiceDetail detailDto : createDto.getDetails()) {
            Product product = productRepository.findById(detailDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + detailDto.getProductId()));

            SalesInvoiceDetail detail = new SalesInvoiceDetail();
            detail.setSalesInvoice(invoice);
            detail.setProduct(product);
            detail.setQuantity(detailDto.getQuantity());
            detail.setUnitPrice(detailDto.getUnitPrice());
            detail.setVatRate(detailDto.getVatRate());
            detail.setDescription(detailDto.getDescription());

            // Hesaplamalar
            BigDecimal lineTotal = detail.getQuantity().multiply(detail.getUnitPrice());
            BigDecimal vatAmount = lineTotal.multiply(detail.getVatRate().divide(new BigDecimal(100)));
            BigDecimal lineNet = lineTotal.add(vatAmount);

            detail.setLineTotal(lineTotal);
            detail.setVatAmount(vatAmount);
            detail.setNetTotal(lineNet);

            subTotal = subTotal.add(lineTotal);
            vatTotal = vatTotal.add(vatAmount);
            netTotal = netTotal.add(lineNet);

            details.add(detail);
        }

        invoice.setSubTotal(subTotal);
        invoice.setVatAmount(vatTotal);
        invoice.setTotal(netTotal);
        invoice.setSalesInvoiceDetails(details);

        SalesInvoice saved = invoiceRepository.save(invoice);
        log.info("Satış faturası oluşturuldu: {}", saved.getInvoiceNo());
        return ApiResponse.success(mapToDto(saved), "Fatura taslak olarak kaydedildi.");
    }

    @Override
    @Transactional
    public ApiResponse<DtoSalesInvoice> updateInvoice(Integer id, DtoUpdateSalesInvoice updateDto) {
        SalesInvoice invoice = invoiceRepository.findByInvoiceId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura bulunamadı: " + id));

        if ("APPROVED".equals(invoice.getStatus())) {
            throw new BusinessRuleException("Onaylanmış fatura güncellenemez.");
        }

        // Başlık güncelleme
        CariAccount cari = cariRepository.findById(updateDto.getCariId()).orElseThrow();
        Warehouse warehouse = warehouseRepository.findById(updateDto.getWarehouseId()).orElseThrow();
        
        invoice.setCariAccount(cari);
        invoice.setWarehouse(warehouse);
        invoice.setInvoiceDate(updateDto.getInvoiceDate());
        invoice.setDueDate(updateDto.getDueDate());
        invoice.setDescription(updateDto.getDescription());

        // Detayları sıfırdan oluştur (Eskileri sil, yenileri ekle mantığı veya güncelleme)
        // Hibernate orphanRemoval=true olduğu için listeyi temizleyip yeniden eklemek en temizidir.
        invoice.getSalesInvoiceDetails().clear();
        
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal vatTotal = BigDecimal.ZERO;
        BigDecimal netTotal = BigDecimal.ZERO;

        for (DtoCreateSalesInvoiceDetail detailDto : updateDto.getDetails()) {
            Product product = productRepository.findById(detailDto.getProductId()).orElseThrow();
            
            SalesInvoiceDetail detail = new SalesInvoiceDetail();
            detail.setSalesInvoice(invoice);
            detail.setProduct(product);
            detail.setQuantity(detailDto.getQuantity());
            detail.setUnitPrice(detailDto.getUnitPrice());
            detail.setVatRate(detailDto.getVatRate());
            
            BigDecimal lineTotal = detail.getQuantity().multiply(detail.getUnitPrice());
            BigDecimal vatAmount = lineTotal.multiply(detail.getVatRate().divide(new BigDecimal(100)));
            BigDecimal lineNet = lineTotal.add(vatAmount);
            
            detail.setLineTotal(lineTotal);
            detail.setVatAmount(vatAmount);
            detail.setNetTotal(lineNet);
            
            invoice.getSalesInvoiceDetails().add(detail);
            
            subTotal = subTotal.add(lineTotal);
            vatTotal = vatTotal.add(vatAmount);
            netTotal = netTotal.add(lineNet);
        }
        
        invoice.setSubTotal(subTotal);
        invoice.setVatAmount(vatTotal);
        invoice.setTotal(netTotal);

        SalesInvoice updated = invoiceRepository.save(invoice);
        return ApiResponse.success(mapToDto(updated), "Fatura güncellendi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteInvoice(Integer id) {
        SalesInvoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura bulunamadı."));

        if ("APPROVED".equals(invoice.getStatus())) {
            throw new BusinessRuleException("Onaylanmış fatura silinemez. İptal işlemi yapınız.");
        }

        invoiceRepository.delete(invoice);
        return ApiResponse.success(true, "Fatura silindi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> approveInvoice(Integer id, DtoInvoiceApproval approvalDto) {
        SalesInvoice invoice = invoiceRepository.findByInvoiceId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura bulunamadı."));

        if (!"DRAFT".equals(invoice.getStatus())) {
            throw new BusinessRuleException("Sadece taslak faturalar onaylanabilir.");
        }

        // 1. Stoktan Düş (Satış = Stok Çıkışı)
        for (SalesInvoiceDetail detail : invoice.getSalesInvoiceDetails()) {
            DtoDetailedUpdateStock stockDto = new DtoDetailedUpdateStock();
            stockDto.setProductId(detail.getProduct().getProductId());
            stockDto.setWarehouseId(invoice.getWarehouse().getWarehouseId());
            stockDto.setQuantity(detail.getQuantity());
            stockDto.setTransactionType("CIKIS"); // Satış -> Çıkış
            stockDto.setUnitPrice(detail.getUnitPrice());
            stockDto.setDocumentNo(invoice.getInvoiceNo());
            stockDto.setNotes("Satış Faturası Onayı");
            
            stockService.updateStockWithTransaction(stockDto);
        }

        // 2. Cari Hesaba Borç Ekle (Müşteri bize borçlandı)
        cariAccountService.updateCariBalanceManual(
                invoice.getCariAccount().getCariId(), 
                invoice.getTotal(), 
                "BORC"); // Müşterinin borcu artar

        invoice.setStatus("APPROVED");
        invoiceRepository.save(invoice);
        log.info("Fatura onaylandı: {}", id);
        return ApiResponse.success(true, "Fatura onaylandı, stok ve cari güncellendi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> cancelInvoice(Integer id, DtoInvoiceCancellation cancelDto) {
        SalesInvoice invoice = invoiceRepository.findByInvoiceId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura bulunamadı."));

        if (!"APPROVED".equals(invoice.getStatus())) {
            throw new BusinessRuleException("Sadece onaylı faturalar iptal edilebilir.");
        }

        // 1. Stoğu İade Al (Stok Girişi)
        for (SalesInvoiceDetail detail : invoice.getSalesInvoiceDetails()) {
            DtoDetailedUpdateStock stockDto = new DtoDetailedUpdateStock();
            stockDto.setProductId(detail.getProduct().getProductId());
            stockDto.setWarehouseId(invoice.getWarehouse().getWarehouseId());
            stockDto.setQuantity(detail.getQuantity());
            stockDto.setTransactionType("GIRIS"); // İptal -> Giriş
            stockDto.setUnitPrice(detail.getUnitPrice());
            stockDto.setDocumentNo(invoice.getInvoiceNo());
            stockDto.setNotes("Fatura İptali: " + cancelDto.getReason());
            
            stockService.updateStockWithTransaction(stockDto);
        }

        // 2. Cari Bakiyeyi Düzelt (Alacak)
        cariAccountService.updateCariBalanceManual(
                invoice.getCariAccount().getCariId(), 
                invoice.getTotal(), 
                "ALACAK"); // Borcu düşer

        invoice.setStatus("CANCELLED");
        invoice.setDescription(invoice.getDescription() + " [IPTAL: " + cancelDto.getReason() + "]");
        invoiceRepository.save(invoice);
        return ApiResponse.success(true, "Fatura iptal edildi.");
    }

    @Override
    public ApiResponse<DtoInvoiceSummary> getInvoiceSummary(LocalDateTime fromDate, LocalDateTime toDate) {
        DtoInvoiceSummary summary = new DtoInvoiceSummary();
        summary.setTotalInvoices((int) invoiceRepository.count());
        summary.setDraftInvoices((int) invoiceRepository.countByStatus("DRAFT"));
        summary.setApprovedInvoices((int) invoiceRepository.countByStatus("APPROVED"));
        summary.setCancelledInvoices((int) invoiceRepository.countByStatus("CANCELLED"));
        summary.setTotalAmount(invoiceRepository.getTotalSalesAmount(fromDate, toDate));
        return ApiResponse.success(summary);
    }

    @Override
    public ApiResponse<BigDecimal> getTotalSalesAmount(LocalDateTime fromDate, LocalDateTime toDate) {
        return ApiResponse.success(invoiceRepository.getTotalSalesAmount(fromDate, toDate));
    }

    @Override
    public ApiResponse<String> generateInvoiceNo() {
        String prefix = "SF" + LocalDateTime.now().getYear();
        String lastNo = invoiceRepository.findLastInvoiceNoByPrefix(prefix);
        
        int nextNum = 1;
        if (lastNo != null) {
            String numPart = lastNo.replace(prefix, "");
            nextNum = Integer.parseInt(numPart) + 1;
        }
        
        return ApiResponse.success(String.format("%s%06d", prefix, nextNum));
    }

    // --- MAPPERS ---
    private DtoSalesInvoice mapToDto(SalesInvoice entity) {
        DtoSalesInvoice dto = new DtoSalesInvoice();
        dto.setInvoiceId(entity.getInvoiceId());
        dto.setInvoiceNo(entity.getInvoiceNo());
        dto.setCariId(entity.getCariAccount().getCariId());
        dto.setCariCode(entity.getCariAccount().getCariCode());
        dto.setCariName(entity.getCariAccount().getCariName());
        dto.setWarehouseId(entity.getWarehouse().getWarehouseId());
        dto.setWarehouseName(entity.getWarehouse().getWarehouseName());
        dto.setInvoiceDate(entity.getInvoiceDate());
        dto.setDueDate(entity.getDueDate());
        dto.setSubTotal(entity.getSubTotal());
        dto.setVatAmount(entity.getVatAmount());
        dto.setTotal(entity.getTotal());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        
        List<DtoSalesInvoiceDetail> details = entity.getSalesInvoiceDetails().stream().map(d -> {
            DtoSalesInvoiceDetail dd = new DtoSalesInvoiceDetail();
            dd.setDetailId(d.getDetailId());
            dd.setInvoiceId(entity.getInvoiceId());
            dd.setProductId(d.getProduct().getProductId());
            dd.setProductCode(d.getProduct().getProductCode());
            dd.setProductName(d.getProduct().getProductName());
            dd.setUnitName(d.getProduct().getUnit().getUnitName());
            dd.setQuantity(d.getQuantity());
            dd.setUnitPrice(d.getUnitPrice());
            dd.setVatRate(d.getVatRate());
            dd.setLineTotal(d.getLineTotal());
            dd.setVatAmount(d.getVatAmount());
            dd.setNetTotal(d.getNetTotal());
            dd.setDescription(d.getDescription());
            return dd;
        }).collect(Collectors.toList());
        
        dto.setDetails(details);
        return dto;
    }
}