package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DtoProductCategory {
    private Integer categoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private boolean isActive;
    private LocalDateTime createdDate;
    private int productCount;
}