package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.dto.LoginDTO;
import com.example.server.dto.LoginResponseDTO;
import com.example.server.dto.SysUserDTO;
import com.example.server.entity.SysUser;
import com.example.server.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller
 */
@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@RequestBody LoginDTO dto,
                                          @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor) {
        String ipAddress = xForwardedFor != null ? xForwardedFor.split(",")[0] : "127.0.0.1";
        LoginResponseDTO response = sysUserService.login(dto, ipAddress);
        return Result.success("登录成功", response);
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public Result<Long> addUser(@RequestBody SysUserDTO dto) {
        Long id = sysUserService.addUser(dto);
        return Result.success("用户添加成功", id);
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/{id}")
    public Result<SysUser> getUser(@PathVariable Long id) {
        SysUser user = sysUserService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody SysUserDTO dto) {
        sysUserService.updateUser(dto);
        return Result.success("用户信息更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success("用户删除成功");
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    public Result<List<SysUser>> listUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long departmentId) {
        List<SysUser> users = sysUserService.listUsers(username, departmentId);
        return Result.success(users);
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/status/{id}")
    public Result<String> updateUserStatus(@PathVariable Long id,
                                           @RequestParam String status) {
        sysUserService.updateUserStatus(id, status);
        return Result.success("用户状态更新成功");
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password/{id}")
    public Result<String> resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return Result.success("密码已重置为123456");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestParam Long userId,
                                         @RequestParam String oldPassword,
                                         @RequestParam String newPassword) {
        sysUserService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
}
