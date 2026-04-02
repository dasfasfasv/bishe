package com.example.server.common;

/**
        * 预约状态枚举
 */
public enum AppointmentStatusEnum {
    PENDING("1", "待审批"),
    APPROVED("2", "已批准"),
    REJECTED("3", "已拒绝"),
    USED("4", "已使用"),
    EXPIRED("5", "已过期"),
    CANCELLED("6", "已取消");

    private String code;
    private String desc;

    AppointmentStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
