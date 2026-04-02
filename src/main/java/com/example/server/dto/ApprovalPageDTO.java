package com.example.server.dto;

import lombok.Data;

/**
 * 审批分页查询 DTO
 */
@Data
public class ApprovalPageDTO {
    private String visitorName;  // 访客名称
    private String departmentName;  // 部门名称
    private String status;  // 审批状态
    private Integer page;  // 页码
    private Integer pageSize;  // 每页大小
}
