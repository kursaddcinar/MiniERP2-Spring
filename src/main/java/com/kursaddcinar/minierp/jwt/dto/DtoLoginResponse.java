package com.kursaddcinar.minierp.jwt.dto;

import lombok.Data;

@Data
public class DtoLoginResponse {
    private String token;
    private DtoUser user;
}