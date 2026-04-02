package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统操作日志实体类
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;  // 操作用户ID
    private String username;  // 操作用户名
    private String operationModule;  // 操作模块
    private String operationType;  // 操作类型：查询、新增、修改、删除
    private String operationDesc;  // 操作描述
    private String requestUrl;  // 请求URL
    private String requestMethod;  // 请求方法：GET、POST等
    private String operationStatus;  // 操作状态：成功、失败
    private String errorMsg;  // 错误信息
    private String ipAddress;  // 操作IP
    private LocalDateTime operationTime;
    private LocalDateTime createTime;
}
