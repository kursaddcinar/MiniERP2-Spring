//get ile okuyacağımız sınıf
package com.kursaddcinar.minierp.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoCariAccount {
    private Integer cariId;
    private String cariCode;
    private String cariName;
    private Integer typeId;
    private String typeName;
    private String taxNo;
    private String taxOffice;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String contactPerson;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;
    private boolean isActive;
    private LocalDateTime createdDate;
    
    // Bu alan veritabanında yok, service katmanında hesaplanıp set edilecek (Alacak/Borç/Sıfır)
    private String balanceType; 
}