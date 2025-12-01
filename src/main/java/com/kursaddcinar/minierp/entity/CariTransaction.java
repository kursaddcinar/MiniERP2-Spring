package com.kursaddcinar.minierp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cari_transactions")
@Getter
@Setter
public class CariTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_type", length = 10, nullable = false)
    private String transactionType;

    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "document_type", length = 20)
    private String documentType;

    @Column(name = "document_no", length = 50)
    private String documentNo;

    // Navigation Properties
    
    // CariID FK karşılığı
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cari_id", nullable = false)
    private CariAccount cariAccount;
}