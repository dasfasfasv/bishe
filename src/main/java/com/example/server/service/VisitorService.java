package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.dto.VisitorDTO;
import com.example.server.entity.Visitor;

import java.util.List;

/**
 * 访客Service接口
 */
public interface VisitorService extends IService<Visitor> {
    /**
     * 保存访客
     */
    Long saveVisitor(VisitorDTO dto);

    /**
     * 查询访客
     */
    Visitor getVisitorById(Long id);

    /**
     * 根据身份证查询访客
     */
    Visitor getVisitorByIdCard(String idCard);

    /**
     * 更新访客
     */
    void updateVisitor(VisitorDTO dto);

    /**
     * 删除访客
     */
    void deleteVisitor(Long id);

    /**
     * 查询访客列表
     */
    List<Visitor> listVisitors(String name, String category);
}
