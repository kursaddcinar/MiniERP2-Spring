package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.util.List;

@Data
public class DtoUpdateUser {
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive = true;
    private List<Integer> roleIds;
}