package com.example.server.common;

/**
 * 审批状态枚举
 */
public enum ApprovalStatusEnum {
    PENDING("1", "待审批"),
    APPROVED("2", "已批准"),
    REJECTED("3", "已拒绝"),
    RETURNED("4", "已退回");

    private String code;
    private String desc;

    ApprovalStatusEnum(String code, String desc) {
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
