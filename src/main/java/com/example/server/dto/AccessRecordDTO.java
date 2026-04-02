package com.example.server.dto;

import lombok.Data;

/**
 * 通行记录DTO
 */
@Data
public class AccessRecordDTO {
    private Long visitorId;
    private String accessType;
    private String accessPoint;
    private String areaCode;
    private String qrCode;
}
