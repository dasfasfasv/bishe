package com.example.server.common;

/**
 * 黑名单类型枚举
 */
public enum BlacklistTypeEnum {
    PERMANENT("1", "永久"),
    TEMPORARY("2", "临时");

    private String code;
    private String desc;

    BlacklistTypeEnum(String code, String desc) {
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
