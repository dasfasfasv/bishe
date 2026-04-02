package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.dto.AccessRecordDTO;
import com.example.server.entity.AccessRecord;
import com.example.server.mapper.AccessRecordMapper;
import com.example.server.service.AccessRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通行记录Service实现类
 */
@Service
public class AccessRecordServiceImpl extends ServiceImpl<AccessRecordMapper, AccessRecord> implements AccessRecordService {

    @Override
    public void recordAccess(AccessRecordDTO dto, String status, String failureReason) {
        AccessRecord record = new AccessRecord();
        record.setVisitorId(dto.getVisitorId());
        record.setAccessType(dto.getAccessType());
        record.setAccessPoint(dto.getAccessPoint());
        record.setAreaCode(dto.getAreaCode());
        record.setQrCode(dto.getQrCode());
        record.setStatus(status);
        record.setFailureReason(failureReason);
        record.setAccessTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());

        save(record);
    }

    @Override
    public List<AccessRecord> listAccessRecords(Long visitorId, String accessType, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        if (visitorId != null) {
            wrapper.eq("visitor_id", visitorId);
        }
        if (accessType != null && !accessType.isEmpty()) {
            wrapper.eq("access_type", accessType);
        }
        if (startTime != null) {
            wrapper.ge("access_time", startTime);
        }
        if (endTime != null) {
            wrapper.le("access_time", endTime);
        }
        wrapper.orderByDesc("access_time");
        return list(wrapper);
    }

    @Override
    public List<AccessRecord> listAbnormalRecords() {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "0");
        wrapper.orderByDesc("access_time");
        return list(wrapper);
    }

    @Override
    public Integer getCurrentPeopleCount() {
        // 查询今天进入且未离开的访客数
        QueryWrapper<AccessRecord> inWrapper = new QueryWrapper<>();
        inWrapper.eq("access_type", "IN")
                .ge("access_time", LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));

        QueryWrapper<AccessRecord> outWrapper = new QueryWrapper<>();
        outWrapper.eq("access_type", "OUT")
                .ge("access_time", LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));

        long inCount = count(inWrapper);
        long outCount = count(outWrapper);

        return (int) (inCount - outCount);
    }
}
