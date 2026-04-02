package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色实体类
 */
@Data
@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleName;  // 角色名称
    private String roleCode;  // 角色代码
    private String description;  // 角色描述
    private String status;  // 状态：1-启用 0-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
