package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.entity.SysRole;

import java.util.List;

/**
 * 角色Service接口
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 添加角色
     */
    Long addRole(SysRole role);

    /**
     * 查询角色列表
     */
    List<SysRole> listRoles();

    /**
     * 更新角色
     */
    void updateRole(SysRole role);

    /**
     * 删除角色
     */
    void deleteRole(Long id);
}
