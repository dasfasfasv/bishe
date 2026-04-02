package com.example.server.VO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批响应 VO（包含关联的预约和访客信息）
 */
@Data
public class ApprovalVO {
    // 审批信息
    private Long id;
    private Long appointmentId;
    private String status;
    private String statusLabel;
    private String remark;
    private LocalDateTime approvalTime;
    private Long approverUserId;
    private String approverName;
    private Integer approvalLevel;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 预约信息
    private String visitReason;
    private LocalDateTime appointmentTime;
    private LocalDateTime appointmentEndTime;
    private String approvalRemark;

    // 访客信息
    private Long visitorId;
    private String visitorName;
    private String visitorPhone;
    private String visitorEmail;
    private String visitorIdCard;
    private String visitorCategory;
    private String creditLevel;

    // 部门信息
    private Long departmentId;
    private String departmentName;

    // 统计字段
    private String approverRole;
}
