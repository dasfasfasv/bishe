package com.example.server.dto;

import lombok.Data;

@Data
public class VisitorDTO {
    private Long id;
    private String name;
    private String idCard;
    private String phone;
    private String email;
    private String category;
    private String creditLevel;
    private String remark;
}
