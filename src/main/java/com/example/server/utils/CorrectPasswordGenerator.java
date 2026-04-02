package com.example.server.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 正确的密码生成工具
 * 用于生成 123456 的正确 BCrypt 哈希值
 */
public class CorrectPasswordGenerator {
    public static void main(String[] args) {
        // 创建编码器
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 原始密码
        String rawPassword = "123456";

        // 生成加密密码（每次运行都会不同，因为 BCrypt 包含随机 salt）
        String hashedPassword1 = encoder.encode(rawPassword);
        String hashedPassword2 = encoder.encode(rawPassword);
        String hashedPassword3 = encoder.encode(rawPassword);

        System.out.println("========================================");
        System.out.println("BCrypt 密码生成工具");
        System.out.println("========================================");
        System.out.println("\n原始密码: " + rawPassword);
        System.out.println("\n生成的加密密码（共3个，用其中任意一个）：");
        System.out.println("\n选项1: " + hashedPassword1);
        System.out.println("选项2: " + hashedPassword2);
        System.out.println("选项3: " + hashedPassword3);

        // 验证
        System.out.println("\n========================================");
        System.out.println("验证结果：");
        System.out.println("========================================");
        System.out.println("验证选项1: " + encoder.matches(rawPassword, hashedPassword1));
        System.out.println("验证选项2: " + encoder.matches(rawPassword, hashedPassword2));
        System.out.println("验证选项3: " + encoder.matches(rawPassword, hashedPassword3));

        // 生成 SQL 更新语句
        System.out.println("\n========================================");
        System.out.println("SQL 更新语句（选择其中一个）：");
        System.out.println("========================================");
        System.out.println("UPDATE sys_user SET password = '" + hashedPassword1 + "';");
        System.out.println("-- 或");
        System.out.println("UPDATE sys_user SET password = '" + hashedPassword2 + "';");
        System.out.println("-- 或");
        System.out.println("UPDATE sys_user SET password = '" + hashedPassword3 + "';");
    }
}