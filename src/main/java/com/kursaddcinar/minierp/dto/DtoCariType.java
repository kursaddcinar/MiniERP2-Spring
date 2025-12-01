package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DtoCariType {
    private Integer typeId;
    private String typeCode;
    private String typeName;
    private String description;
    private boolean isActive;
    private LocalDateTime createdDate;
    private int cariAccountCount; // Repository'den count ile doldurulacak
}