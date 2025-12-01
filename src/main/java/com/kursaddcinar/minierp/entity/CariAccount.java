package com.kursaddcinar.minierp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cari_accounts")
@Getter
@Setter
public class CariAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cari_id")
    private Integer cariId;

    @Column(name = "cari_code", length = 20, nullable = false)
    private String cariCode;

    @Column(name = "cari_name", length = 150, nullable = false)
    private String cariName;

    @Column(name = "tax_no", length = 20)
    private String taxNo;

    @Column(name = "tax_office", length = 100)
    private String taxOffice;

    @Column(name = "address")
    private String address;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "credit_limit", precision = 18, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "current_balance", precision = 18, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "is_active")
    private boolean isActive = true;

    // Navigation Properties
    
    // TypeID FK karşılığı
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private CariType type;

    @OneToMany(mappedBy = "cariAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CariTransaction> cariTransactions = new ArrayList<>();

    // TO:DO
    // @OneToMany(mappedBy = "cariAccount")
    // private List<SalesInvoice> salesInvoices = new ArrayList<>();
    
    // @OneToMany(mappedBy = "cariAccount")
    // private List<PurchaseInvoice> purchaseInvoices = new ArrayList<>();
}