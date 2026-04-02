package com.example.server.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 门禁授权DTO
 */
@Data
public class AccessAuthorizationDTO {
    private Long appointmentId;
    private Long visitorId;
    private String allowedAreas;
    private LocalDateTime effectiveTime;
    private LocalDateTime expiryTime;
}
