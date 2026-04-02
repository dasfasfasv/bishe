package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 门禁授权实体类
 */
@Data
@TableName("access_authorization")
public class AccessAuthorization {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long appointmentId;  // 预约ID
    private Long visitorId;  // 访客ID
    private String accessCard;  // 门禁卡号
    private String qrCode;  // 二维码
    private String allowedAreas;  // 允许进入的区域（JSON格式）
    private LocalDateTime effectiveTime;  // 生效时间
    private LocalDateTime expiryTime;  // 失效时间
    private String status;  // 状态：1-有效 0-无效
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
