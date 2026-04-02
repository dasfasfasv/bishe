package com.example.server.controller;

import com.example.server.VO.ApprovalStatisticsVO;
import com.example.server.VO.ApprovalVO;
import com.example.server.common.Result;
import com.example.server.dto.ApprovalPageDTO;
import com.example.server.entity.Approval;
import com.example.server.service.ApprovalService;
import com.example.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批 Controller
 */
@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 查询待审批列表（当前用户的）
     */
    @GetMapping("/pending")
    public Result<List<ApprovalVO>> listPendingApprovals(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        List<ApprovalVO> approvals = approvalService.listPendingApprovals(userId);
        return Result.success(approvals);
    }

    /**
     * 分页查询审批列表
     */
    @GetMapping("/list")
    public Result<List<ApprovalVO>> listApprovals(
            @RequestParam(required = false) String visitorName,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ApprovalPageDTO pageDTO = new ApprovalPageDTO();
        pageDTO.setVisitorName(visitorName);
        pageDTO.setDepartmentName(departmentName);
        pageDTO.setStatus(status);
        pageDTO.setPage(page);
        pageDTO.setPageSize(pageSize);

        List<ApprovalVO> approvals = approvalService.listApprovalsByPage(pageDTO);
        return Result.success(approvals);
    }

    /**
     * 查询审批详情
     */
    @GetMapping("/{id}")
    public Result<ApprovalVO> getApprovalDetail(@PathVariable Long id) {
        ApprovalVO approval = approvalService.getApprovalDetail(id);
        return Result.success(approval);
    }

    /**
     * 查询审批历史
     */
    @GetMapping("/history/{appointmentId}")
    public Result<List<ApprovalVO>> getApprovalHistory(@PathVariable Long appointmentId) {
        List<ApprovalVO> history = approvalService.getApprovalHistory(appointmentId);
        return Result.success(history);
    }

    /**
     * 批准预约
     */
    @PostMapping("/approve/{appointmentId}")
    public Result<String> approveAppointment(
            @PathVariable Long appointmentId,
            @RequestParam(required = false) String remark,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        approvalService.approveAppointment(appointmentId, remark, userId);
        return Result.success("预约已批准");
    }

    /**
     * 拒绝预约
     */
    @PostMapping("/reject/{appointmentId}")
    public Result<String> rejectAppointment(
            @PathVariable Long appointmentId,
            @RequestParam String remark,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        approvalService.rejectAppointment(appointmentId, remark, userId);
        return Result.success("预约已拒绝");
    }

    /**
     * 退回预约
     */
    @PostMapping("/return/{appointmentId}")
    public Result<String> returnAppointment(
            @PathVariable Long appointmentId,
            @RequestParam String remark,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        approvalService.returnAppointment(appointmentId, remark, userId);
        return Result.success("预约已退回");
    }

    /**
     * 获取审批统计
     */
    @GetMapping("/statistics")
    public Result<ApprovalStatisticsVO> getApprovalStatistics(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        ApprovalStatisticsVO statistics = approvalService.getApprovalStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 批量审批
     */
    @PostMapping("/batch")
    public Result<String> batchApprove(
            @RequestParam List<Long> appointmentIds,
            @RequestParam String status,
            @RequestParam(required = false) String remark,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        approvalService.batchApprove(appointmentIds, status, remark, userId);
        return Result.success("批量审批完成");
    }

    /**
     * 从 Token 中提取用户ID
     */
    private Long extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return 1L;  // 默认用户
        }

        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        return userId != null ? userId : 1L;
    }
}
