package com.example.server.utils;


import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;

/**
 * JWT 密钥生成工具
 * 用于生成符合 HS512 安全要求的密钥
 */
public class JwtKeyGenerator {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("JWT 密钥生成工具");
        System.out.println("========================================\n");

        // 生成符合 HS512 要求的密钥
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(keyBytes);

        System.out.println("生成的密钥信息：");
        System.out.println("- 密钥长度: " + (keyBytes.length * 8) + " bits");
        System.out.println("- 密钥大小: " + keyBytes.length + " 字节");
        System.out.println("- Base64 编码后: " + encodedKey);
        System.out.println();
        System.out.println("========================================");
        System.out.println("复制以下密钥到配置文件：");
        System.out.println("========================================");
        System.out.println(encodedKey);
        System.out.println();
    }
}
