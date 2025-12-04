package com.kursaddcinar.minierp.service.impl;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.entity.*;
import com.kursaddcinar.minierp.exception.BusinessRuleException;
import com.kursaddcinar.minierp.exception.ResourceNotFoundException;
import com.kursaddcinar.minierp.repository.*;
import com.kursaddcinar.minierp.service.ICariAccountService;
import com.kursaddcinar.minierp.service.IPurchaseInvoiceService;
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
public class PurchaseInvoiceServiceImpl implements IPurchaseInvoiceService {

    private final PurchaseInvoiceRepository invoiceRepository;
    private final CariAccountRepository cariRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    
    // Stok ve Cari servisleri
    private final IStockService stockService;
    private final ICariAccountService cariAccountService;

    @Override
    public ApiResponse<Page<DtoPurchaseInvoice>> getInvoices(Pageable pageable, String status, LocalDateTime startDate, LocalDateTime endDate, Integer cariId) {
        // İleride Specification ile filtreleme eklenebilir. Şimdilik findAll.
        Page<PurchaseInvoice> page = invoiceRepository.findAll(pageable);
        return ApiResponse.success(page.map(this::mapToDto));
    }

    @Override
    public ApiResponse<DtoPurchaseInvoice> getInvoiceById(Integer id) {
        PurchaseInvoice invoice = invoiceRepository.findByInvoiceId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alış faturası bulunamadı: " + id));
        return ApiResponse.success(mapToDto(invoice));
    }

    @Override
    public ApiResponse<DtoPurchaseInvoice> getInvoiceByNo(String invoiceNo) {
        PurchaseInvoice invoice = invoiceRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new ResourceNotFoundException("Alış faturası bulunamadı: " + invoiceNo));
        return ApiResponse.success(mapToDto(invoice));
    }

    @Override
    @Transactional
    public ApiResponse<DtoPurchaseInvoice> createInvoice(DtoCreatePurchaseInvoice createDto) {
        if (invoiceRepository.existsByInvoiceNo(createDto.getInvoiceNo())) {
            throw new BusinessRuleException("Bu fatura numarası zaten kullanılıyor.");
        }

        CariAccount cari = cariRepository.findById(createDto.getCariId())
                .orElseThrow(() -> new ResourceNotFoundException("Cari bulunamadı."));
        Warehouse warehouse = warehouseRepository.findById(createDto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Depo bulunamadı."));

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setInvoiceNo(createDto.getInvoiceNo());
        invoice.setCariAccount(cari);
        invoice.setWarehouse(warehouse);
        invoice.setInvoiceDate(createDto.getInvoiceDate());
        invoice.setDueDate(createDto.getDueDate());
        invoice.setDescription(createDto.getDescription());
        invoice.setStatus("DRAFT");

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal vatTotal = BigDecimal.ZERO;
        BigDecimal netTotal = BigDecimal.ZERO;

        List<PurchaseInvoiceDetail> details = new ArrayList<>();
        for (DtoCreatePurchaseInvoiceDetail detailDto : createDto.getDetails()) {
            Product product = productRepository.findById(detailDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + detailDto.getProductId()));

            PurchaseInvoiceDetail detail = new PurchaseInvoiceDetail();
            detail.setPurchaseInvoice(invoice);
            detail.setProduct(product);
            detail.setQuantity(detailDto.getQuantity());
            detail.setUnitPrice(detailDto.getUnitPrice());
            detail.setVatRate(detailDto.getVatRate());
            detail.setDescription(detailDto.getDescription());

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
        invoice.setPurchaseInvoiceDetails(details);

        PurchaseInvoice saved = invoiceRepository.save(invoice);
        log.info("Alış faturası oluşturuldu: {}", saved.getInvoiceNo());
        return ApiResponse.success(mapToDto(saved), "Alış faturası taslak olarak kaydedildi.");
    }

    @Override
    @Transactional
    public ApiResponse<DtoPurchaseInvoice> updateInvoice(Integer id, DtoUpdatePurchaseInvoice updateDto) {
        PurchaseInvoice invoice = invoiceRepository.findByInvoiceId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alış faturası bulunamadı: " + id));

        if ("APPROVED".equals(invoice.getStatus())) {
            throw new BusinessRuleException("Onaylanmış fatura güncellenemez.");
        }

        CariAccount cari = cariRepository.findById(updateDto.getCariId()).orElseThrow();
        Warehouse warehouse = warehouseRepository.findById(updateDto.getWarehouseId()).orElseThrow();
        
        invoice.setCariAccount(cari);
        invoice.setWarehouse(warehouse);
        invoice.setInvoiceDate(updateDto.getInvoiceDate());
        invoice.setDueDate(updateDto.getDueDate());
        invoice.setDescription(updateDto.getDescription());

        // Detayları temizle ve yeniden oluştur
        invoice.getPurchaseInvoiceDetails().clear();
        
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal vatTotal = BigDecimal.ZERO;
        BigDecimal netTotal = BigDecimal.ZERO;

        for (DtoCreatePurchaseInvoiceDetail detailDto : updateDto.getDetails()) {
            Product product = productRepository.findById(detailDto.getProductId()).orElseThrow();
            
            PurchaseInvoiceDetail detail = new PurchaseInvoiceDetail();
            detail.setPurchaseInvoice(invoice);
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
            
            invoice.getPurchaseInvoiceDetails().add(detail);
            
            subTotal = subTotal.add(lineTotal);
            vatTotal = vatTotal.add(vatAmount);
            netTotal = netTotal.add(lineNet);
        }
        
        invoice.setSubTotal(subTotal);
        invoice.setVatAmount(vatTotal);
        invoice.setTotal(netTotal);

        PurchaseInvoice updated = invoiceRepository.save(invoice);
        return ApiResponse.success(mapToDto(updated), "Alış faturası güncellendi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteInvoice(Integer id) {
        PurchaseInvoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alış faturası bulunamadı."));

        if ("APPROVED".equals(invoice.getStatus())) {
            throw new BusinessRuleException("Onaylanmış fatura silinemez. İptal işlemi yapınız.");
        }

        invoiceRepository.delete(invoice);
        return ApiResponse.success(true, "Alış faturası silindi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> approveInvoice(Integer id, DtoInvoiceApproval approvalDto) {
        PurchaseInvoice invoice = invoiceRepository.findByInvoiceId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alış faturası bulunamadı."));

        if (!"DRAFT".equals(invoice.getStatus())) {
            throw new BusinessRuleException("Sadece taslak faturalar onaylanabilir.");
        }

        // 1. Stok Girişi Yap (Mal aldık, stok artar)
        for (PurchaseInvoiceDetail detail : invoice.getPurchaseInvoiceDetails()) {
            DtoDetailedUpdateStock stockDto = new DtoDetailedUpdateStock();
            stockDto.setProductId(detail.getProduct().getProductId());
            stockDto.setWarehouseId(invoice.getWarehouse().getWarehouseId());
            stockDto.setQuantity(detail.getQuantity());
            stockDto.setTransactionType("GIRIS"); // ALIŞ = GİRİŞ
            stockDto.setUnitPrice(detail.getUnitPrice());
            stockDto.setDocumentNo(invoice.getInvoiceNo());
            stockDto.setNotes("Alış Faturası Onayı");
            
            stockService.updateStockWithTransaction(stockDto);
        }

        // 2. Cari Hesaba Alacak Ekle (Tedarikçiye borçlandık)
        cariAccountService.updateCariBalanceManual(
                invoice.getCariAccount().getCariId(), 
                invoice.getTotal(), 
                "ALACAK"); // Tedarikçinin alacağı artar (Bizim borcumuz)

        invoice.setStatus("APPROVED");
        invoiceRepository.save(invoice);
        log.info("Alış faturası onaylandı: {}", id);
        return ApiResponse.success(true, "Alış faturası onaylandı, stok ve cari güncellendi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> cancelInvoice(Integer id, DtoInvoiceCancellation cancelDto) {
        PurchaseInvoice invoice = invoiceRepository.findByInvoiceId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alış faturası bulunamadı."));

        if (!"APPROVED".equals(invoice.getStatus())) {
            throw new BusinessRuleException("Sadece onaylı faturalar iptal edilebilir.");
        }

        // 1. Stoğu Geri Çık (Stok Çıkışı - İade mantığı)
        for (PurchaseInvoiceDetail detail : invoice.getPurchaseInvoiceDetails()) {
            DtoDetailedUpdateStock stockDto = new DtoDetailedUpdateStock();
            stockDto.setProductId(detail.getProduct().getProductId());
            stockDto.setWarehouseId(invoice.getWarehouse().getWarehouseId());
            stockDto.setQuantity(detail.getQuantity());
            stockDto.setTransactionType("CIKIS"); // İptal -> Çıkış
            stockDto.setUnitPrice(detail.getUnitPrice());
            stockDto.setDocumentNo(invoice.getInvoiceNo());
            stockDto.setNotes("Alış Faturası İptali: " + cancelDto.getReason());
            
            stockService.updateStockWithTransaction(stockDto);
        }

        // 2. Cari Bakiyeyi Düzelt (Borç Düşülür -> Borç kaydı atılır)
        cariAccountService.updateCariBalanceManual(
                invoice.getCariAccount().getCariId(), 
                invoice.getTotal(), 
                "BORC"); // Tedarikçinin alacağı düşer

        invoice.setStatus("CANCELLED");
        invoice.setDescription(invoice.getDescription() + " [IPTAL: " + cancelDto.getReason() + "]");
        invoiceRepository.save(invoice);
        return ApiResponse.success(true, "Alış faturası iptal edildi.");
    }

    @Override
    public ApiResponse<DtoInvoiceSummary> getInvoiceSummary(LocalDateTime fromDate, LocalDateTime toDate) {
        DtoInvoiceSummary summary = new DtoInvoiceSummary();
        summary.setTotalInvoices((int) invoiceRepository.count());
        summary.setDraftInvoices((int) invoiceRepository.countByStatus("DRAFT"));
        summary.setApprovedInvoices((int) invoiceRepository.countByStatus("APPROVED"));
        summary.setCancelledInvoices((int) invoiceRepository.countByStatus("CANCELLED"));
        summary.setTotalAmount(invoiceRepository.getTotalPurchaseAmount(fromDate, toDate));
        return ApiResponse.success(summary);
    }

    @Override
    public ApiResponse<BigDecimal> getTotalPurchaseAmount(LocalDateTime fromDate, LocalDateTime toDate) {
        return ApiResponse.success(invoiceRepository.getTotalPurchaseAmount(fromDate, toDate));
    }

    @Override
    public ApiResponse<String> generateInvoiceNo() {
        String prefix = "AF" + LocalDateTime.now().getYear(); // AF = Alış Faturası
        String lastNo = invoiceRepository.findLastInvoiceNoByPrefix(prefix);
        
        int nextNum = 1;
        if (lastNo != null) {
            String numPart = lastNo.replace(prefix, "");
            nextNum = Integer.parseInt(numPart) + 1;
        }
        
        return ApiResponse.success(String.format("%s%06d", prefix, nextNum));
    }

    // --- MAPPERS ---
    private DtoPurchaseInvoice mapToDto(PurchaseInvoice entity) {
        DtoPurchaseInvoice dto = new DtoPurchaseInvoice();
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
        
        List<DtoPurchaseInvoiceDetail> details = entity.getPurchaseInvoiceDetails().stream().map(d -> {
            DtoPurchaseInvoiceDetail dd = new DtoPurchaseInvoiceDetail();
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