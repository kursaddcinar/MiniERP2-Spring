package com.kursaddcinar.minierp.service.impl;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.entity.*;
import com.kursaddcinar.minierp.exception.BusinessRuleException;
import com.kursaddcinar.minierp.exception.ResourceNotFoundException;
import com.kursaddcinar.minierp.repository.*;
import com.kursaddcinar.minierp.service.IStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements IStockService {

    private final StockCardRepository stockCardRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    // ==========================================
    // STOCK CARD OPERATIONS
    // ==========================================

    @Override
    public ApiResponse<Page<DtoStockCard>> getStockCards(Pageable pageable) {
        // Arama desteği eklemek istersek repository metodunu buraya bağlarız.
        // Şimdilik genel listeyi dönüyorum.
        Page<StockCard> page = stockCardRepository.findAll(pageable);
        return ApiResponse.success(page.map(this::mapToDtoStockCard));
    }

    @Override
    public ApiResponse<DtoStockCard> getStockCardById(Integer id) {
        StockCard stockCard = stockCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stok kartı bulunamadı: " + id));
        return ApiResponse.success(mapToDtoStockCard(stockCard));
    }

    @Override
    public ApiResponse<DtoStockCard> getStockCardByProductAndWarehouse(Integer productId, Integer warehouseId) {
        StockCard stockCard = stockCardRepository.findByProductProductIdAndWarehouseWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Bu ürün ve depo için stok kartı bulunamadı."));
        return ApiResponse.success(mapToDtoStockCard(stockCard));
    }

    @Override
    public ApiResponse<List<DtoStockCard>> getStockCardsByProductId(Integer productId) {
        List<StockCard> list = stockCardRepository.findByProductProductId(productId);
        return ApiResponse.success(list.stream().map(this::mapToDtoStockCard).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<DtoStockCard>> getStockCardsByWarehouseId(Integer warehouseId) {
        List<StockCard> list = stockCardRepository.findByWarehouseWarehouseId(warehouseId);
        return ApiResponse.success(list.stream().map(this::mapToDtoStockCard).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public ApiResponse<DtoStockCard> createStockCard(DtoCreateStockCard createDto) {
        // Zaten var mı kontrolü
        if (stockCardRepository.findByProductProductIdAndWarehouseWarehouseId(createDto.getProductId(), createDto.getWarehouseId()).isPresent()) {
            throw new BusinessRuleException("Bu ürün ve depo için zaten bir stok kartı mevcut.");
        }

        Product product = productRepository.findById(createDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + createDto.getProductId()));
        
        Warehouse warehouse = warehouseRepository.findById(createDto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Depo bulunamadı: " + createDto.getWarehouseId()));

        StockCard stockCard = new StockCard();
        stockCard.setProduct(product);
        stockCard.setWarehouse(warehouse);
        stockCard.setCurrentStock(createDto.getCurrentStock());
        stockCard.setReservedStock(createDto.getReservedStock());
        
        StockCard saved = stockCardRepository.save(stockCard);
        log.info("Stok kartı oluşturuldu. ID: {}", saved.getStockCardId());
        return ApiResponse.success(mapToDtoStockCard(saved), "Stok kartı oluşturuldu.");
    }

    @Override
    @Transactional
    public ApiResponse<DtoStockCard> updateStockCard(Integer id, DtoUpdateStockCard updateDto) {
        StockCard stockCard = stockCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stok kartı bulunamadı: " + id));

        if (updateDto.getCurrentStock() != null) stockCard.setCurrentStock(updateDto.getCurrentStock());
        if (updateDto.getReservedStock() != null) stockCard.setReservedStock(updateDto.getReservedStock());

        StockCard saved = stockCardRepository.save(stockCard);
        return ApiResponse.success(mapToDtoStockCard(saved), "Stok kartı güncellendi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteStockCard(Integer id) {
        StockCard stockCard = stockCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stok kartı bulunamadı: " + id));
        
        // Eğer stok varsa silinmemeli
        if (stockCard.getCurrentStock().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("Bakiyesi olan stok kartı silinemez.");
        }

        stockCardRepository.delete(stockCard);
        return ApiResponse.success(true, "Stok kartı silindi.");
    }

    // ==========================================
    // STOCK STATUS & UPDATE OPERATIONS
    // ==========================================

    @Override
    public ApiResponse<List<DtoStockCard>> getCriticalStockCards() {
        return ApiResponse.success(stockCardRepository.findCriticalStockCards()
                .stream().map(this::mapToDtoStockCard).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<DtoStockCard>> getOverStockCards() {
        return ApiResponse.success(stockCardRepository.findOverStockCards()
                .stream().map(this::mapToDtoStockCard).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<DtoStockCard>> getOutOfStockCards() {
        return ApiResponse.success(stockCardRepository.findByCurrentStockLessThanEqual(BigDecimal.ZERO)
                .stream().map(this::mapToDtoStockCard).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> updateStock(Integer productId, Integer warehouseId, BigDecimal quantity, String transactionType) {
        return updateStockInternal(productId, warehouseId, quantity, transactionType);
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> updateStockWithTransaction(DtoDetailedUpdateStock updateDto) {
        // 1. Stok Kartını Güncelle
        updateStockInternal(updateDto.getProductId(), updateDto.getWarehouseId(), updateDto.getQuantity(), updateDto.getTransactionType());

        // 2. Hareket Kaydı (Log) Oluştur
        createTransactionInternal(updateDto);

        return ApiResponse.success(true, "Stok güncellendi ve hareket kaydı oluşturuldu.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> reserveStock(Integer productId, Integer warehouseId, BigDecimal quantity) {
        StockCard stockCard = getOrCreateStockCard(productId, warehouseId);
        
        // Kullanılabilir stok kontrolü (Current - Reserved)
        BigDecimal available = stockCard.getCurrentStock().subtract(stockCard.getReservedStock());
        if (available.compareTo(quantity) < 0) {
            throw new BusinessRuleException("Yetersiz stok! Mevcut kullanılabilir stok: " + available);
        }

        stockCard.setReservedStock(stockCard.getReservedStock().add(quantity));
        stockCardRepository.save(stockCard);
        return ApiResponse.success(true, "Stok rezerve edildi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> releaseReservedStock(Integer productId, Integer warehouseId, BigDecimal quantity) {
        StockCard stockCard = getOrCreateStockCard(productId, warehouseId);

        if (stockCard.getReservedStock().compareTo(quantity) < 0) {
            throw new BusinessRuleException("Serbest bırakılacak miktar, rezerve miktardan fazla olamaz.");
        }

        stockCard.setReservedStock(stockCard.getReservedStock().subtract(quantity));
        stockCardRepository.save(stockCard);
        return ApiResponse.success(true, "Rezerve stok serbest bırakıldı.");
    }

    // ==========================================
    // STOCK TRANSACTION OPERATIONS
    // ==========================================

    @Override
    public ApiResponse<Page<DtoStockTransaction>> getStockTransactions(Pageable pageable) {
        return ApiResponse.success(stockTransactionRepository.findAll(pageable).map(this::mapToDtoStockTransaction));
    }

    @Override
    public ApiResponse<DtoStockTransaction> getStockTransactionById(Integer id) {
        StockTransaction transaction = stockTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hareket bulunamadı: " + id));
        return ApiResponse.success(mapToDtoStockTransaction(transaction));
    }

    @Override
    @Transactional
    public ApiResponse<DtoStockTransaction> createStockTransaction(DtoCreateStockTransaction createDto) {
        // DtoDetailedUpdateStock yapısına çevirip ortak metodu kullanabiliriz veya manuel yapabiliriz.
        // Manuel yapalım, kontrol bizde olsun.
        
        // 1. Stoğu Güncelle
        updateStockInternal(createDto.getProductId(), createDto.getWarehouseId(), createDto.getQuantity(), createDto.getTransactionType());

        // 2. Hareketi Kaydet
        Product product = productRepository.findById(createDto.getProductId()).orElseThrow();
        Warehouse warehouse = warehouseRepository.findById(createDto.getWarehouseId()).orElseThrow();

        StockTransaction txn = new StockTransaction();
        txn.setProduct(product);
        txn.setWarehouse(warehouse);
        txn.setTransactionDate(createDto.getTransactionDate());
        txn.setTransactionType(createDto.getTransactionType());
        txn.setQuantity(createDto.getQuantity());
        txn.setUnitPrice(createDto.getUnitPrice());
        txn.setTotalAmount(createDto.getQuantity().multiply(createDto.getUnitPrice()));
        txn.setDescription(createDto.getDescription());
        txn.setDocumentType(createDto.getDocumentType());
        txn.setDocumentNo(createDto.getDocumentNo());

        StockTransaction saved = stockTransactionRepository.save(txn);
        return ApiResponse.success(mapToDtoStockTransaction(saved), "Stok hareketi kaydedildi.");
    }

    @Override
    public ApiResponse<List<DtoStockTransaction>> getTransactionsByProductId(Integer productId) {
        return ApiResponse.success(stockTransactionRepository.findByProductProductIdOrderByTransactionDateDesc(productId)
                .stream().map(this::mapToDtoStockTransaction).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<DtoStockTransaction>> getTransactionsByWarehouseId(Integer warehouseId) {
        return ApiResponse.success(stockTransactionRepository.findByWarehouseWarehouseIdOrderByTransactionDateDesc(warehouseId)
                .stream().map(this::mapToDtoStockTransaction).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<DtoStockTransaction>> getTransactionsByStockCardId(Integer stockCardId) {
        StockCard sc = stockCardRepository.findById(stockCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Stok kartı bulunamadı."));
        // Bu stok kartına ait (Ürün + Depo kombinasyonu) hareketleri bulmamız lazım. 
        // Repository'de buna özel metod yoksa stream ile filtreleyebiliriz veya Repo'ya metod ekleyebiliriz.
        // Şimdilik Product ID ile çekip Warehouse ID ile filtreliyorum (Performans için Repo metodu daha iyi olurdu).
        List<StockTransaction> list = stockTransactionRepository.findByProductProductIdOrderByTransactionDateDesc(sc.getProduct().getProductId())
                .stream()
                .filter(t -> t.getWarehouse().getWarehouseId().equals(sc.getWarehouse().getWarehouseId()))
                .collect(Collectors.toList());
        
        return ApiResponse.success(list.stream().map(this::mapToDtoStockTransaction).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<DtoStockTransaction>> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return ApiResponse.success(stockTransactionRepository.findByTransactionDateBetweenOrderByTransactionDateDesc(startDate, endDate)
                .stream().map(this::mapToDtoStockTransaction).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<DtoStockTransaction>> getIncomingTransactions(LocalDateTime fromDate, LocalDateTime toDate) {
        // Repo'da metod imzası farklı olabilir, kontrol edip uyarlıyorum
        // Repo metodumuz: findByTransactionDateBetween... ve manuel filtre
        // Performans için repo'ya özel metod yazmıştık sanırım? Evet, değilse stream.
        // Repo adımında `GetIncomingTransactionsAsync` benzeri bir query yazmamıştık, o yüzden stream ile yapıyorum.
        return ApiResponse.success(stockTransactionRepository.findByTransactionDateBetweenOrderByTransactionDateDesc(fromDate, toDate)
                .stream()
                .filter(t -> "GIRIS".equals(t.getTransactionType()))
                .map(this::mapToDtoStockTransaction)
                .collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<DtoStockTransaction>> getOutgoingTransactions(LocalDateTime fromDate, LocalDateTime toDate) {
        return ApiResponse.success(stockTransactionRepository.findByTransactionDateBetweenOrderByTransactionDateDesc(fromDate, toDate)
                .stream()
                .filter(t -> "CIKIS".equals(t.getTransactionType()))
                .map(this::mapToDtoStockTransaction)
                .collect(Collectors.toList()));
    }

    // ==========================================
    // STOCK MOVEMENT (TRANSFER)
    // ==========================================

    @Override
    @Transactional
    public ApiResponse<Boolean> processStockMovement(DtoCreateStockMovement movementDto) {
        // 1. Çıkış İşlemi (Kaynak Depo)
        DtoDetailedUpdateStock outDto = new DtoDetailedUpdateStock();
        outDto.setProductId(movementDto.getProductId());
        outDto.setWarehouseId(movementDto.getFromWarehouseId());
        outDto.setQuantity(movementDto.getQuantity());
        outDto.setTransactionType("CIKIS");
        outDto.setDocumentNo("TRF-" + System.currentTimeMillis());
        outDto.setNotes("Transfer Çıkış: " + movementDto.getDescription());
        outDto.setUnitPrice(BigDecimal.ZERO); // Transferde maliyet fiyatı taşınır normalde ama şimdilik 0
        
        updateStockWithTransaction(outDto);

        // 2. Giriş İşlemi (Hedef Depo)
        DtoDetailedUpdateStock inDto = new DtoDetailedUpdateStock();
        inDto.setProductId(movementDto.getProductId());
        inDto.setWarehouseId(movementDto.getToWarehouseId());
        inDto.setQuantity(movementDto.getQuantity());
        inDto.setTransactionType("GIRIS");
        inDto.setDocumentNo(outDto.getDocumentNo()); // Aynı belge no
        inDto.setNotes("Transfer Giriş: " + movementDto.getDescription());
        inDto.setUnitPrice(BigDecimal.ZERO);

        updateStockWithTransaction(inDto);

        return ApiResponse.success(true, "Transfer başarıyla gerçekleşti.");
    }

    // ==========================================
    // WAREHOUSE OPERATIONS
    // ==========================================

    @Override
    public ApiResponse<Page<DtoWarehouse>> getWarehouses(Pageable pageable) {
        return ApiResponse.success(warehouseRepository.findAll(pageable).map(this::mapToDtoWarehouse));
    }

    @Override
    public ApiResponse<DtoWarehouse> getWarehouseById(Integer id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depo bulunamadı: " + id));
        return ApiResponse.success(mapToDtoWarehouse(warehouse));
    }

    @Override
    public ApiResponse<DtoWarehouse> getWarehouseByCode(String warehouseCode) {
        Warehouse warehouse = warehouseRepository.findByWarehouseCode(warehouseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Depo bulunamadı: " + warehouseCode));
        return ApiResponse.success(mapToDtoWarehouse(warehouse));
    }

    @Override
    @Transactional
    public ApiResponse<DtoWarehouse> createWarehouse(DtoCreateWarehouse createDto) {
        if (warehouseRepository.existsByWarehouseCode(createDto.getWarehouseCode())) {
            throw new BusinessRuleException("Bu depo kodu zaten kullanılıyor.");
        }

        Warehouse w = new Warehouse();
        w.setWarehouseCode(createDto.getWarehouseCode());
        w.setWarehouseName(createDto.getWarehouseName());
        w.setAddress(createDto.getAddress());
        w.setCity(createDto.getCity());
        w.setResponsiblePerson(createDto.getResponsiblePerson());
        w.setActive(true);

        Warehouse saved = warehouseRepository.save(w);
        return ApiResponse.success(mapToDtoWarehouse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<DtoWarehouse> updateWarehouse(Integer id, DtoUpdateWarehouse updateDto) {
        Warehouse w = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depo bulunamadı: " + id));
        
        w.setWarehouseName(updateDto.getWarehouseName());
        w.setAddress(updateDto.getAddress());
        w.setCity(updateDto.getCity());
        w.setResponsiblePerson(updateDto.getResponsiblePerson());
        w.setActive(updateDto.isActive());

        Warehouse saved = warehouseRepository.save(w);
        return ApiResponse.success(mapToDtoWarehouse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteWarehouse(Integer id) {
        Warehouse w = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depo bulunamadı: " + id));
        
        // Stok kontrolü yapılmalı
        if (!w.getStockCards().isEmpty()) {
             throw new BusinessRuleException("İçinde stok kaydı olan depo silinemez.");
        }

        warehouseRepository.delete(w);
        return ApiResponse.success(true, "Depo silindi.");
    }

    @Override
    public ApiResponse<List<DtoWarehouse>> getActiveWarehouses() {
        return ApiResponse.success(warehouseRepository.findByIsActiveTrueOrderByWarehouseName()
                .stream().map(this::mapToDtoWarehouse).collect(Collectors.toList()));
    }

    // ==========================================
    // REPORTS & STATISTICS
    // ==========================================

    @Override
    public ApiResponse<DtoStockSummary> getStockSummary() {
        DtoStockSummary summary = new DtoStockSummary();
        summary.setTotalProducts((int) productRepository.count());
        summary.setActiveProducts(productRepository.findByIsActiveTrue().size());
        summary.setTotalStockValue(productRepository.getTotalStockValue());
        
        // Diğer istatistikler repo sorgularıyla doldurulabilir
        // Performans notu: Bu tür özet ekranlar için genelde tek bir native query veya view kullanılır.
        return ApiResponse.success(summary);
    }

    @Override
    public ApiResponse<DtoStockReport> getStockReport(Integer warehouseId, Integer categoryId) {
        // Burada dinamik filtreleme gerekir. Basitlik adına tümünü çekip stream ile filtreliyorum.
        // Gerçekte Specification kullanılmalı.
        List<StockCard> allCards = stockCardRepository.findAll();
        
        List<DtoStockCard> filtered = allCards.stream()
                .filter(sc -> warehouseId == null || sc.getWarehouse().getWarehouseId().equals(warehouseId))
                .filter(sc -> categoryId == null || (sc.getProduct().getCategory() != null && sc.getProduct().getCategory().getCategoryId().equals(categoryId)))
                .map(this::mapToDtoStockCard)
                .collect(Collectors.toList());

        DtoStockReport report = new DtoStockReport();
        report.setReportDate(LocalDateTime.now());
        report.setReportType("Genel Stok Raporu");
        report.setStockCards(filtered);
        report.setTotalItems(filtered.size());
        // Toplam değer hesaplama
        // report.setTotalValue(...) 
        
        return ApiResponse.success(report);
    }

    @Override
    public ApiResponse<BigDecimal> getTotalStockValue() {
        return ApiResponse.success(productRepository.getTotalStockValue());
    }

    @Override
    public ApiResponse<BigDecimal> getTotalStockValueByWarehouse(Integer warehouseId) {
        return ApiResponse.success(warehouseRepository.getTotalStockValueByWarehouseId(warehouseId));
    }

    @Override
    public ApiResponse<BigDecimal> getTotalIncomingValue(LocalDateTime fromDate, LocalDateTime toDate) {
        return ApiResponse.success(stockTransactionRepository.getTotalValueByTypeAndDateBetween("GIRIS", fromDate, toDate));
    }

    @Override
    public ApiResponse<BigDecimal> getTotalOutgoingValue(LocalDateTime fromDate, LocalDateTime toDate) {
        return ApiResponse.success(stockTransactionRepository.getTotalValueByTypeAndDateBetween("CIKIS", fromDate, toDate));
    }

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    private StockCard getOrCreateStockCard(Integer productId, Integer warehouseId) {
        return stockCardRepository.findByProductProductIdAndWarehouseWarehouseId(productId, warehouseId)
                .orElseGet(() -> {
                    StockCard sc = new StockCard();
                    sc.setProduct(productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Ürün yok")));
                    sc.setWarehouse(warehouseRepository.findById(warehouseId).orElseThrow(() -> new ResourceNotFoundException("Depo yok")));
                    sc.setCurrentStock(BigDecimal.ZERO);
                    sc.setReservedStock(BigDecimal.ZERO);
                    return stockCardRepository.save(sc);
                });
    }

    private ApiResponse<Boolean> updateStockInternal(Integer productId, Integer warehouseId, BigDecimal quantity, String type) {
        StockCard stockCard = getOrCreateStockCard(productId, warehouseId);

        if ("GIRIS".equalsIgnoreCase(type)) {
            stockCard.setCurrentStock(stockCard.getCurrentStock().add(quantity));
        } else if ("CIKIS".equalsIgnoreCase(type)) {
            if (stockCard.getCurrentStock().compareTo(quantity) < 0) {
                throw new BusinessRuleException("Yetersiz stok! Mevcut: " + stockCard.getCurrentStock());
            }
            stockCard.setCurrentStock(stockCard.getCurrentStock().subtract(quantity));
        } else {
            throw new BusinessRuleException("Geçersiz işlem tipi: " + type);
        }
        
        stockCard.setLastTransactionDate(LocalDateTime.now());
        stockCardRepository.save(stockCard);
        return ApiResponse.success(true);
    }

    private void createTransactionInternal(DtoDetailedUpdateStock dto) {
        StockTransaction txn = new StockTransaction();
        txn.setProduct(productRepository.findById(dto.getProductId()).orElseThrow());
        txn.setWarehouse(warehouseRepository.findById(dto.getWarehouseId()).orElseThrow());
        txn.setTransactionDate(LocalDateTime.now());
        txn.setTransactionType(dto.getTransactionType());
        txn.setQuantity(dto.getQuantity());
        txn.setUnitPrice(dto.getUnitPrice());
        if (dto.getUnitPrice() != null) {
            txn.setTotalAmount(dto.getQuantity().multiply(dto.getUnitPrice()));
        }
        txn.setDescription(dto.getNotes());
        txn.setDocumentNo(dto.getDocumentNo());
        
        stockTransactionRepository.save(txn);
    }

    // --- MAPPERS ---
    private DtoStockCard mapToDtoStockCard(StockCard entity) {
        DtoStockCard dto = new DtoStockCard();
        dto.setStockCardId(entity.getStockCardId());
        dto.setProductId(entity.getProduct().getProductId());
        dto.setProductName(entity.getProduct().getProductName());
        dto.setProductCode(entity.getProduct().getProductCode());
        dto.setWarehouseId(entity.getWarehouse().getWarehouseId());
        dto.setWarehouseName(entity.getWarehouse().getWarehouseName());
        dto.setUnitName(entity.getProduct().getUnit().getUnitName());
        dto.setCurrentStock(entity.getCurrentStock());
        dto.setReservedStock(entity.getReservedStock());
        dto.setAvailableStock(entity.getCurrentStock().subtract(entity.getReservedStock()));
        dto.setLastTransactionDate(entity.getLastTransactionDate());
        return dto;
    }

    private DtoStockTransaction mapToDtoStockTransaction(StockTransaction entity) {
        DtoStockTransaction dto = new DtoStockTransaction();
        dto.setTransactionId(entity.getTransactionId());
        dto.setProductId(entity.getProduct().getProductId());
        dto.setProductName(entity.getProduct().getProductName());
        dto.setWarehouseName(entity.getWarehouse().getWarehouseName());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setTransactionType(entity.getTransactionType());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setDocumentNo(entity.getDocumentNo());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private DtoWarehouse mapToDtoWarehouse(Warehouse entity) {
        DtoWarehouse dto = new DtoWarehouse();
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setWarehouseCode(entity.getWarehouseCode());
        dto.setWarehouseName(entity.getWarehouseName());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setResponsiblePerson(entity.getResponsiblePerson());
        dto.setActive(entity.isActive());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}