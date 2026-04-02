package com.example.server.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.server.entity.Visitor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 访客Mapper
 */
@Mapper
public interface VisitorMapper extends BaseMapper<Visitor> {
}
