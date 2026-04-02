package com.example.server.dto;

import lombok.Data;

/**
 * 用户DTO
 */
@Data
public class SysUserDTO {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private Long departmentId;
    private String role;
}
