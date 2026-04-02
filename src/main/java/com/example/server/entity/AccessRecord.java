package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("access_record")
public class AccessRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long visitorId;  // 访客ID
    private String visitorName;  // 访客姓名
    private String accessType;  // 通行类型：IN-进 OUT-出
    private String accessPoint;  // 门禁点
    private String areaCode;  // 区域代码
    private String qrCode;  // 二维码
    private String status;  // 状态：1-成功 0-失败
    private String failureReason;  // 失败原因
    private LocalDateTime accessTime;  // 通行时间
    private LocalDateTime createTime;
}
