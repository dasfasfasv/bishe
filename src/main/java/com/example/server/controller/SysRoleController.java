package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.entity.SysRole;
import com.example.server.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色Controller
 */
@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 添加角色
     */
    @PostMapping("/add")
    public Result<Long> addRole(@RequestBody SysRole role) {
        Long id = sysRoleService.addRole(role);
        return Result.success("角色添加成功", id);
    }

    /**
     * 查询角色列表
     */
    @GetMapping("/list")
    public Result<List<SysRole>> listRoles() {
        List<SysRole> roles = sysRoleService.listRoles();
        return Result.success(roles);
    }

    /**
     * 更新角色
     */
    @PutMapping("/update")
    public Result<String> updateRole(@RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return Result.success("角色更新成功");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteRole(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success("角色删除成功");
    }
}
