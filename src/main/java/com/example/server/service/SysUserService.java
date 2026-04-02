package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.dto.LoginDTO;
import com.example.server.dto.LoginResponseDTO;
import com.example.server.dto.SysUserDTO;
import com.example.server.entity.SysUser;

import java.util.List;

/**
 * 用户Service接口
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 用户登录
     */
    LoginResponseDTO login(LoginDTO dto, String ipAddress);

    /**
     * 查询用户
     */
    SysUser getUserById(Long id);

    /**
     * 根据用户名查询用户
     */
    SysUser getUserByUsername(String username);

    /**
     * 添加用户
     */
    Long addUser(SysUserDTO dto);

    /**
     * 更新用户
     */
    void updateUser(SysUserDTO dto);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 查询用户列表
     */
    List<SysUser> listUsers(String username, Long departmentId);

    /**
     * 启用/禁用用户
     */
    void updateUserStatus(Long id, String status);

    /**
     * 重置密码
     */
    void resetPassword(Long id);

    /**
     * 修改密码
     */
    void changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 验证用户权限
     */
    boolean hasPermission(Long userId, String permissionCode);
}
