package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 黑名单实体类
 */
@Data
@TableName("blacklist")
public class Blacklist {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long visitorId;  // 访客ID
    private String name;  // 姓名
    private String idCard;  // 身份证号
    private String type;  // 类型：1-永久 2-临时
    private String reason;  // 加入原因
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;  // 结束时间（临时黑名单）
    private String status;  // 状态：1-有效 0-无效
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
