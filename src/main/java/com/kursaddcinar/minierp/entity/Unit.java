package com.kursaddcinar.minierp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "units")
@Getter
@Setter
public class Unit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "unit_code", length = 10, nullable = false)
    private String unitCode;

    @Column(name = "unit_name", length = 50, nullable = false)
    private String unitName;

    @Column(name = "is_active")
    private boolean isActive = true;

    // Navigation Properties
    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}