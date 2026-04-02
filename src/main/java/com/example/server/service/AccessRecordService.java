package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.dto.AccessRecordDTO;
import com.example.server.entity.AccessRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通行记录Service接口
 */
public interface AccessRecordService extends IService<AccessRecord> {
    /**
     * 记录通行
     */
    void recordAccess(AccessRecordDTO dto, String status, String failureReason);

    /**
     * 查询通行记录
     */
    List<AccessRecord> listAccessRecords(Long visitorId, String accessType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询异常通行
     */
    List<AccessRecord> listAbnormalRecords();

    /**
     * 查询实时园区人数
     */
    Integer getCurrentPeopleCount();
}
