package com.kursaddcinar.minierp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_code", length = 30, nullable = false)
    private String productCode;

    @Column(name = "product_name", length = 150, nullable = false)
    private String productName;

    // Fiyat alanları (Precision 18, Scale 2)
    @Column(name = "sale_price", precision = 18, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO;

    @Column(name = "purchase_price", precision = 18, scale = 2)
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    @Column(name = "vat_rate", precision = 5, scale = 2)
    private BigDecimal vatRate = new BigDecimal("18.00");

    // Stok limitleri (Adet/Miktar olduğu için Scale 3 yapılabilir)
    @Column(name = "min_stock_level", precision = 18, scale = 3)
    private BigDecimal minStockLevel = BigDecimal.ZERO;

    @Column(name = "max_stock_level", precision = 18, scale = 3)
    private BigDecimal maxStockLevel = BigDecimal.ZERO;

    @Column(name = "is_active")
    private boolean isActive = true;

    // Navigation Properties (FK)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // Nullable
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    // Child Collections
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<StockCard> stockCards = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<StockTransaction> stockTransactions = new ArrayList<>();

    // Fatura modülü eklendiğinde açılacak
    // @OneToMany(mappedBy = "product")
    // private List<SalesInvoiceDetail> salesInvoiceDetails;
    
    // @OneToMany(mappedBy = "product")
    // private List<PurchaseInvoiceDetail> purchaseInvoiceDetails;
}