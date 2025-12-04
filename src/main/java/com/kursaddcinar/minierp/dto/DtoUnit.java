package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DtoUnit {
    private Integer unitId;
    private String unitCode;
    private String unitName;
    private boolean isActive;
    private LocalDateTime createdDate;
    private int productCount; // Service'de doldurulacak
}