package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long visitorId;  // 访客ID
    private String visitorName;  // 访客姓名
    private String visitorPhone;  // 访客电话
    private Long departmentId;  // 访问部门ID
    private String departmentName;  // 访问部门名称
    private String visitReason;  // 访问事由
    private LocalDateTime appointmentTime;  // 预约时间
    private LocalDateTime appointmentEndTime;  // 预约结束时间
    private String status;  // 状态：1-待审批 2-已批准 3-已拒绝 4-已使用 5-已过期 6-已取消
    private String approvalRemark;  // 审批备注
    private LocalDateTime approvalTime;  // 审批时间
    private Long approvalUserId;  // 审批人ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

