package com.kursaddcinar.minierp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "warehouse_code", length = 20, nullable = false)
    private String warehouseCode;

    @Column(name = "warehouse_name", length = 100, nullable = false)
    private String warehouseName;

    @Column(name = "address")
    private String address;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "responsible_person", length = 100)
    private String responsiblePerson;

    @Column(name = "is_active")
    private boolean isActive = true;

    // Navigation Properties
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private List<StockCard> stockCards = new ArrayList<>();

    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private List<StockTransaction> stockTransactions = new ArrayList<>();
    
    // Fatura detayları eklendiğinde açılacak:
    // private List<SalesInvoice> salesInvoices...
    // private List<PurchaseInvoice> purchaseInvoices...
}