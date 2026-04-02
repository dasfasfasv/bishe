package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统权限实体类
 */
@Data
@TableName("sys_permission")
public class SysPermission {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String permissionName;  // 权限名称
    private String permissionCode;  // 权限代码
    private String resourceType;  // 资源类型：菜单、操作、接口等
    private String resourceUrl;  // 资源URL
    private String description;  // 权限描述
    private String status;  // 状态：1-启用 0-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
