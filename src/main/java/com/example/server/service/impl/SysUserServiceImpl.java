package com.example.server.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.dto.LoginDTO;
import com.example.server.dto.LoginResponseDTO;
import com.example.server.dto.SysUserDTO;
import com.example.server.entity.SysUser;
import com.example.server.exception.BusinessException;
import com.example.server.mapper.SysUserMapper;
import com.example.server.service.SysUserService;
import com.example.server.utils.JwtUtil;
import com.example.server.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Service实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public LoginResponseDTO login(LoginDTO dto, String ipAddress) {
        // 1. 查询用户
        SysUser user = getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 检查用户状态
        if ("0".equals(user.getStatus())) {
            throw new BusinessException("该用户已被禁用，无法登录");
        }

        // 3. 验证密码（关键！）
        // 前端传入：dto.getPassword() = "admin@123456"（明文）
        // 数据库存储：user.getPassword() = 加密后的密码
        if (!PasswordUtil.verifyPassword(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 5. 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        updateById(user);

        // 6. 构建响应
        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setToken(token);
        response.setExpiresIn(jwtUtil.getExpirationTime());

        return response;
    }

    @Override
    public SysUser getUserById(Long id) {
        return getById(id);
    }

    @Override
    public SysUser getUserByUsername(String username) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return getOne(wrapper);
    }

    @Override
    public Long addUser(SysUserDTO dto) {
        // 检查用户名是否存在
        SysUser existing = getUserByUsername(dto.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);

        // 密码加密！
        user.setPassword(PasswordUtil.encryptPassword(dto.getPassword()));
        user.setStatus("1");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        save(user);
        return user.getId();
    }

    @Override
    public void updateUser(SysUserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);

        // 如果前端传了密码，需要加密
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encryptPassword(dto.getPassword()));
        }

        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        removeById(id);
    }

    @Override
    public List<SysUser> listUsers(String username, Long departmentId) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        if (departmentId != null) {
            wrapper.eq("department_id", departmentId);
        }
        wrapper.eq("status", "1");
        return list(wrapper);
    }

    @Override
    public void updateUserStatus(Long id, String status) {
        SysUser user = getById(id);
        if (user != null) {
            user.setStatus(status);
            user.setUpdateTime(LocalDateTime.now());
            updateById(user);
        }
    }

    @Override
    public void resetPassword(Long id) {
        SysUser user = getById(id);
        if (user != null) {
            // 重置为默认密码：123456
            user.setPassword(PasswordUtil.encryptPassword("123456"));
            user.setUpdateTime(LocalDateTime.now());
            updateById(user);
        }
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证原密码
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 设置新密码
        user.setPassword(PasswordUtil.encryptPassword(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        // TODO: 实现权限检查逻辑
        return true;
    }
}