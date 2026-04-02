package com.example.server.dto;

import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponseDTO {
    private Long userId;
    private String username;
    private String realName;
    private String token;
    private Long expiresIn;  // 过期时间（秒）
}
