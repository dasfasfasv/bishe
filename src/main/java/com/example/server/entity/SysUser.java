package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体类
 */
@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;  // 用户名
    private String password;  // 密码（加密存储）
    private String realName;  // 真实姓名
    private String email;  // 邮箱
    private String phone;  // 电话
    private Long departmentId;  // 部门ID
    private String status;  // 状态：1-启用 0-禁用
    private String role;  // 角色（逗号分隔）
    private LocalDateTime lastLoginTime;  // 最后登录时间
    private String lastLoginIp;  // 最后登录IP
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
