package com.example.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.entity.SysRole;
import com.example.server.mapper.SysRoleMapper;
import com.example.server.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色Service实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public Long addRole(SysRole role) {
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        save(role);
        return role.getId();
    }

    @Override
    public List<SysRole> listRoles() {
        return list();
    }

    @Override
    public void updateRole(SysRole role) {
        role.setUpdateTime(LocalDateTime.now());
        updateById(role);
    }

    @Override
    public void deleteRole(Long id) {
        removeById(id);
    }
}
