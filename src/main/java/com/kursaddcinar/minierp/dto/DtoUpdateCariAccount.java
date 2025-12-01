package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoUpdateCariAccount {
    
    @NotBlank(message = "Cari adı zorunludur")
    @Size(max = 200, message = "Cari adı en fazla 200 karakter olabilir")
    private String cariName;
    
    @NotNull(message = "Cari türü seçilmelidir")
    private Integer typeId;
    
    @Size(max = 15, message = "Vergi numarası en fazla 15 karakter olabilir")
    private String taxNo;
    
    @Size(max = 100, message = "Vergi dairesi en fazla 100 karakter olabilir")
    private String taxOffice;
    
    @Size(max = 500, message = "Adres en fazla 500 karakter olabilir")
    private String address;
    
    @Size(max = 50, message = "Şehir en fazla 50 karakter olabilir")
    private String city;
    
    @Size(max = 20, message = "Telefon en fazla 20 karakter olabilir")
    private String phone;
    
    @Email(message = "Geçerli bir email adresi giriniz")
    @Size(max = 100, message = "Email en fazla 100 karakter olabilir")
    private String email;
    
    @Size(max = 100, message = "İletişim kişisi en fazla 100 karakter olabilir")
    private String contactPerson;
    
    @DecimalMin(value = "0.0", message = "Kredi limiti 0'dan küçük olamaz")
    @DecimalMax(value = "9999999999.0", message = "Kredi limiti çok yüksek")
    private BigDecimal creditLimit;
    
    private boolean isActive = true;
}