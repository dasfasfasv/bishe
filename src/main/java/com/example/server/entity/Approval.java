package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批实体类
 */
@Data
@TableName("approval")
public class Approval {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long appointmentId;  // 预约ID
    private Long approverUserId;  // 审批人ID
    private String approverName;  // 审批人名称
    private String status;  // 审批状态：1-待审批 2-已批准 3-已拒绝 4-已退回
    private String remark;  // 审批备注
    private LocalDateTime approvalTime;  // 审批时间
    private Integer approvalLevel;  // 审批级别
    private String nextApprover;  // 下一级审批人
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

