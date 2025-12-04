package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoCreateWarehouse {
    @NotBlank(message = "Depo kodu zorunludur")
    private String warehouseCode;

    @NotBlank(message = "Depo adı zorunludur")
    private String warehouseName;
    
    private String address;
    private String city;
    private String responsiblePerson;
}