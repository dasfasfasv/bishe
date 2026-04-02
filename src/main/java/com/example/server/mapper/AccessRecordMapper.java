package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.entity.AccessRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通行记录Mapper
 */
@Mapper
public interface AccessRecordMapper extends BaseMapper<AccessRecord> {
}
