package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoUpdateWarehouse {
    @NotBlank(message = "Depo adı zorunludur")
    private String warehouseName;
    
    private String address;
    private String city;
    private String responsiblePerson;
    private boolean isActive = true;
}