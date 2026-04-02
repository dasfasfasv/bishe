package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.entity.AccessAuthorization;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessAuthorizationMapper extends BaseMapper<AccessAuthorization> {
}
