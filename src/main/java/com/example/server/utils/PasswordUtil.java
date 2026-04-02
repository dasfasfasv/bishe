package com.example.server.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * 密码加密工具类（使用 BCrypt）
 */
public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     * @param rawPassword 原始密码（明文）
     * @return 加密后的密码
     */
    public static String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码（明文）
     * @param encryptedPassword 加密后的密码（数据库存储的）
     * @return 是否匹配
     */
    public static boolean verifyPassword(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            return false;
        }
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            return false;
        }
        return encoder.matches(rawPassword, encryptedPassword);
    }

    /**
     * 生成测试密码（用于初始化默认密码）
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String generateTestPassword(String password) {
        return encryptPassword(password);
    }
}
