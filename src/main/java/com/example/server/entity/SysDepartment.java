package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统部门实体类
 */
@Data
@TableName("sys_department")
public class SysDepartment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String deptName;  // 部门名称
    private String deptCode;  // 部门代码
    private Long parentId;  // 父部门ID
    private String leader;  // 部门负责人
    private String phone;  // 部门电话
    private String email;  // 部门邮箱
    private String status;  // 状态：1-启用 0-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
