package com.example.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 访客实体类
 */
@Data
@TableName("visitor")
public class Visitor {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String idCard;
    private String phone;
    private String email;
    private String category;  // 访客分类：商务、物流、家属等
    private String creditLevel;  // 信用等级
    private Integer visitCount;  // 访问次数
    private LocalDateTime lastVisitTime;  // 最后访问时间
    private String remark;
    private Integer status;  // 1:启用 0:禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
