package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.dto.AccessRecordDTO;
import com.example.server.entity.AccessRecord;
import com.example.server.service.AccessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通行记录Controller
 */
@RestController
@RequestMapping("/api/record")
public class AccessRecordController {

    @Autowired
    private AccessRecordService accessRecordService;

    /**
     * 记录通行
     */
    @PostMapping("/record")
    public Result<String> recordAccess(@RequestBody AccessRecordDTO dto,
                                       @RequestParam String status,
                                       @RequestParam(required = false) String failureReason) {
        accessRecordService.recordAccess(dto, status, failureReason);
        return Result.success("通行记录已保存");
    }

    /**
     * 查询通行记录
     */
    @GetMapping("/list")
    public Result<List<AccessRecord>> listAccessRecords(
            @RequestParam(required = false) Long visitorId,
            @RequestParam(required = false) String accessType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        LocalDateTime startDateTime = startTime != null ? LocalDateTime.parse(startTime) : null;
        LocalDateTime endDateTime = endTime != null ? LocalDateTime.parse(endTime) : null;

        List<AccessRecord> records = accessRecordService.listAccessRecords(visitorId, accessType, startDateTime, endDateTime);
        return Result.success(records);
    }

    /**
     * 查询异常通行记录
     */
    @GetMapping("/abnormal")
    public Result<List<AccessRecord>> listAbnormalRecords() {
        List<AccessRecord> records = accessRecordService.listAbnormalRecords();
        return Result.success(records);
    }

    /**
     * 查询当前园区人数
     */
    @GetMapping("/current-count")
    public Result<Integer> getCurrentPeopleCount() {
        Integer count = accessRecordService.getCurrentPeopleCount();
        return Result.success(count);
    }
}
