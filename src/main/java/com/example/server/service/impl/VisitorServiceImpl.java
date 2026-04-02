package com.example.server.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.dto.VisitorDTO;
import com.example.server.entity.Visitor;
import com.example.server.exception.BusinessException;
import com.example.server.mapper.VisitorMapper;
import com.example.server.service.VisitorService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Override
    public Long saveVisitor(VisitorDTO dto) {
        // 检查身份证是否已存在
        Visitor existing = getVisitorByIdCard(dto.getIdCard());
        if (existing != null) {
            throw new BusinessException("该身份证信息已存在");
        }

        Visitor visitor = new Visitor();
        BeanUtils.copyProperties(dto, visitor);
        visitor.setCreateTime(LocalDateTime.now());
        visitor.setUpdateTime(LocalDateTime.now());
        visitor.setStatus(1);
        visitor.setVisitCount(0);
        visitor.setCreditLevel("A");

        save(visitor);
        return visitor.getId();
    }

    @Override
    public Visitor getVisitorById(Long id) {
        return getById(id);
    }

    @Override
    public Visitor getVisitorByIdCard(String idCard) {
        QueryWrapper<Visitor> wrapper = new QueryWrapper<>();
        wrapper.eq("id_card", idCard);
        return getOne(wrapper);
    }

    @Override
    public void updateVisitor(VisitorDTO dto) {
        Visitor visitor = new Visitor();
        BeanUtils.copyProperties(dto, visitor);
        visitor.setUpdateTime(LocalDateTime.now());
        updateById(visitor);
    }

    @Override
    public void deleteVisitor(Long id) {
        removeById(id);
    }

    @Override
    public List<Visitor> listVisitors(String name, String category) {
        QueryWrapper<Visitor> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        wrapper.eq("status", 1);
        return list(wrapper);
    }
}
