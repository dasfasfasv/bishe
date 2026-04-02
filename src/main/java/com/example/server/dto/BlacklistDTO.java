package com.example.server.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 黑名单DTO
 */
@Data
public class BlacklistDTO {
    private Long visitorId;
    private String name;
    private String idCard;
    private String type;
    private String reason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
