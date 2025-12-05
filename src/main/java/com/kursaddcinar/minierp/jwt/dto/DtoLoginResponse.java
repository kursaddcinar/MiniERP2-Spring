package com.kursaddcinar.minierp.jwt.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DtoLoginResponse {
    private String token;
    private DtoUser user;
}