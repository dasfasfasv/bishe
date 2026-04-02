package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.entity.Blacklist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 黑名单Mapper
 */
@Mapper
public interface BlacklistMapper extends BaseMapper<Blacklist> {
}
