package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统登录日志实体类
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;  // 用户ID
    private String username;  // 用户名
    private String loginStatus;  // 登录状态：成功、失败
    private String failureReason;  // 失败原因
    private String ipAddress;  // 登录IP
    private String userAgent;  // 用户代理
    private LocalDateTime loginTime;
    private LocalDateTime createTime;
}
