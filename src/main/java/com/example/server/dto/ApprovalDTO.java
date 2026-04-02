package com.example.server.dto;

import lombok.Data;

/**
 * 审批DTO
 */
@Data
public class ApprovalDTO {
    private Long appointmentId;
    private String status;  // 2-批准 3-拒绝 4-退回
    private String remark;
}
