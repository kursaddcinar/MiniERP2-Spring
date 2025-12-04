package com.kursaddcinar.minierp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_cards")
@Getter
@Setter
public class StockCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_card_id")
    private Integer stockCardId;

    @Column(name = "current_stock", precision = 18, scale = 3)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "reserved_stock", precision = 18, scale = 3)
    private BigDecimal reservedStock = BigDecimal.ZERO;

    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;

    // Navigation Properties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
}