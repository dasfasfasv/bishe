package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.VO.ApprovalStatisticsVO;
import com.example.server.VO.ApprovalVO;
import com.example.server.dto.ApprovalDTO;
import com.example.server.dto.ApprovalPageDTO;
import com.example.server.entity.Appointment;
import com.example.server.entity.Approval;
import com.example.server.entity.SysUser;
import com.example.server.exception.BusinessException;
import com.example.server.mapper.ApprovalMapper;
import com.example.server.service.AppointmentService;
import com.example.server.service.ApprovalService;
import com.example.server.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审批 Service 实现类
 */
@Service
public class ApprovalServiceImpl extends ServiceImpl<ApprovalMapper, Approval> implements ApprovalService {

    // 使用 @Lazy 注解延迟加载
    @Autowired
    @Lazy
    private AppointmentService appointmentService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ApprovalMapper approvalMapper;

    // ... 其他方法保持不变 ...

    @Override
    @Transactional
    public void approveAppointment(Long appointmentId, String remark, Long userId) {
        // 验证预约存在
        Appointment appointment = appointmentService.getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        if (!"1".equals(appointment.getStatus())) {
            throw new BusinessException("该预约已被处理，无法再次批准");
        }

        // 获取审批人信息
        SysUser approver = sysUserService.getById(userId);
        if (approver == null) {
            throw new BusinessException("审批人不存在");
        }

        // 创建审批记录
        Approval approval = new Approval();
        approval.setAppointmentId(appointmentId);
        approval.setApproverUserId(userId);
        approval.setApproverName(approver.getRealName());
        approval.setStatus("2");  // 已批准
        approval.setRemark(remark);
        approval.setApprovalTime(LocalDateTime.now());
        approval.setApprovalLevel(1);
        approval.setCreateTime(LocalDateTime.now());
        approval.setUpdateTime(LocalDateTime.now());

        save(approval);

        // 更新预约状态
        appointment.setStatus("2");  // 已批准
        appointment.setApprovalRemark(remark);
        appointment.setApprovalTime(LocalDateTime.now());
        appointment.setApprovalUserId(userId);
        appointment.setUpdateTime(LocalDateTime.now());

        appointmentService.updateById(appointment);
    }

    @Override
    @Transactional
    public void rejectAppointment(Long appointmentId, String remark, Long userId) {
        // 验证预约存在
        Appointment appointment = appointmentService.getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        if (!"1".equals(appointment.getStatus())) {
            throw new BusinessException("该预约已被处理，无法再次拒绝");
        }

        // 获取审批人信息
        SysUser approver = sysUserService.getById(userId);
        if (approver == null) {
            throw new BusinessException("审批人不存在");
        }

        // 验证拒绝原因
        if (remark == null || remark.trim().isEmpty()) {
            throw new BusinessException("拒绝原因不能为空");
        }

        // 创建审批记录
        Approval approval = new Approval();
        approval.setAppointmentId(appointmentId);
        approval.setApproverUserId(userId);
        approval.setApproverName(approver.getRealName());
        approval.setStatus("3");  // 已拒绝
        approval.setRemark(remark);
        approval.setApprovalTime(LocalDateTime.now());
        approval.setApprovalLevel(1);
        approval.setCreateTime(LocalDateTime.now());
        approval.setUpdateTime(LocalDateTime.now());

        save(approval);

        // 更新预约状态
        appointment.setStatus("3");  // 已拒绝
        appointment.setApprovalRemark(remark);
        appointment.setApprovalTime(LocalDateTime.now());
        appointment.setApprovalUserId(userId);
        appointment.setUpdateTime(LocalDateTime.now());

        appointmentService.updateById(appointment);
    }

    @Override
    @Transactional
    public void returnAppointment(Long appointmentId, String remark, Long userId) {
        // 验证预约存在
        Appointment appointment = appointmentService.getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        if (!"2".equals(appointment.getStatus())) {
            throw new BusinessException("只有已批准的预约才能退回");
        }

        // 获取审批人信息
        SysUser approver = sysUserService.getById(userId);
        if (approver == null) {
            throw new BusinessException("审批人不存在");
        }

        // 创建审批记录
        Approval approval = new Approval();
        approval.setAppointmentId(appointmentId);
        approval.setApproverUserId(userId);
        approval.setApproverName(approver.getRealName());
        approval.setStatus("4");  // 已退回
        approval.setRemark(remark);
        approval.setApprovalTime(LocalDateTime.now());
        approval.setApprovalLevel(1);
        approval.setCreateTime(LocalDateTime.now());
        approval.setUpdateTime(LocalDateTime.now());

        save(approval);

        // 更新预约状态为待审批
        appointment.setStatus("1");  // 待审批
        appointment.setApprovalRemark(remark);
        appointment.setUpdateTime(LocalDateTime.now());

        appointmentService.updateById(appointment);
    }

    @Override
    public ApprovalStatisticsVO getApprovalStatistics(Long userId) {
        QueryWrapper<Approval> wrapper = new QueryWrapper<>();
        wrapper.eq("approver_user_id", userId);

        List<Approval> approvals = list(wrapper);

        int pendingCount = (int) approvals.stream().filter(a -> "1".equals(a.getStatus())).count();
        int approvedCount = (int) approvals.stream().filter(a -> "2".equals(a.getStatus())).count();
        int rejectedCount = (int) approvals.stream().filter(a -> "3".equals(a.getStatus())).count();
        int totalCount = approvals.size();

        ApprovalStatisticsVO stats = new ApprovalStatisticsVO();
        stats.setPendingCount(pendingCount);
        stats.setApprovedCount(approvedCount);
        stats.setRejectedCount(rejectedCount);
        stats.setTotalCount(totalCount);

        // 计算批准率和拒绝率
        int processedCount = approvedCount + rejectedCount;
        if (processedCount > 0) {
            stats.setApprovalRate((double) approvedCount / processedCount * 100);
            stats.setRejectRate((double) rejectedCount / processedCount * 100);
        }

        // 计算平均审批时间
        if (!approvals.isEmpty()) {
            long totalMinutes = approvals.stream()
                    .filter(a -> a.getApprovalTime() != null)
                    .mapToLong(a -> ChronoUnit.MINUTES.between(a.getCreateTime(), a.getApprovalTime()))
                    .sum();
            stats.setAvgApprovalTime(totalMinutes / approvals.size());
        }

        return stats;
    }

    @Override
    @Transactional
    public void createApprovalWorkflow(Long appointmentId) {
        // 验证预约存在
        Appointment appointment = appointmentService.getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        // 检查是否已有审批流程
        QueryWrapper<Approval> wrapper = new QueryWrapper<>();
        wrapper.eq("appointment_id", appointmentId);
        if (count(wrapper) > 0) {
            throw new BusinessException("该预约已存在审批流程");
        }

        // 获取部门的审批人员（这里简化处理，实际应该根据配置来）
        SysUser approver = sysUserService.getById(1L);  // 默认指定为管理员

        if (approver == null) {
            throw new BusinessException("未配置审批人");
        }

        // 创建审批记录
        Approval approval = new Approval();
        approval.setAppointmentId(appointmentId);
        approval.setApproverUserId(approver.getId());
        approval.setApproverName(approver.getRealName());
        approval.setStatus("1");  // 待审批
        approval.setApprovalLevel(1);
        approval.setCreateTime(LocalDateTime.now());
        approval.setUpdateTime(LocalDateTime.now());

        save(approval);
    }

    @Override
    @Transactional
    public void batchApprove(List<Long> appointmentIds, String status, String remark, Long userId) {
        if (appointmentIds == null || appointmentIds.isEmpty()) {
            throw new BusinessException("预约列表不能为空");
        }

        for (Long appointmentId : appointmentIds) {
            try {
                if ("2".equals(status)) {
                    approveAppointment(appointmentId, remark, userId);
                } else if ("3".equals(status)) {
                    rejectAppointment(appointmentId, remark, userId);
                }
            } catch (Exception e) {
                // 继续处理其他预约，但记录错误
                System.err.println("批量审批失败，预约ID: " + appointmentId + "，错误: " + e.getMessage());
            }
        }
    }

    @Override
    public List<ApprovalVO> listPendingApprovals(Long userId) {
        // 获取当前用户的待审批列表
        QueryWrapper<Approval> wrapper = new QueryWrapper<>();
        wrapper.eq("approver_user_id", userId)
//                .eq("status", "1")
                .orderByDesc("create_time");

        List<Approval> approvals = list(wrapper);

        // 转换为 VO 并补充关联信息
        return approvals.stream()
                .map(a -> approvalMapper.selectApprovalVO(a.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApprovalVO> listApprovalsByPage(ApprovalPageDTO pageDTO) {
        QueryWrapper<Approval> wrapper = new QueryWrapper<>();

        // 状态过滤
        if (pageDTO.getStatus() != null && !pageDTO.getStatus().isEmpty()) {
            wrapper.eq("status", pageDTO.getStatus());
        }

        wrapper.orderByDesc("create_time");

        List<Approval> approvals = list(wrapper);

        // 转换为 VO
        List<ApprovalVO> vos = approvals.stream()
                .map(a -> approvalMapper.selectApprovalVO(a.getId()))
                .collect(Collectors.toList());

        // 前端搜索字段过滤
        if (pageDTO.getVisitorName() != null && !pageDTO.getVisitorName().isEmpty()) {
            vos = vos.stream()
                    .filter(v -> v.getVisitorName() != null && v.getVisitorName().contains(pageDTO.getVisitorName()))
                    .collect(Collectors.toList());
        }

        if (pageDTO.getDepartmentName() != null && !pageDTO.getDepartmentName().isEmpty()) {
            vos = vos.stream()
                    .filter(v -> v.getDepartmentName() != null && v.getDepartmentName().contains(pageDTO.getDepartmentName()))
                    .collect(Collectors.toList());
        }

        // 分页处理
        int page = pageDTO.getPage() != null ? pageDTO.getPage() : 1;
        int pageSize = pageDTO.getPageSize() != null ? pageDTO.getPageSize() : 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, vos.size());

        return vos.subList(start, Math.min(end, vos.size()));
    }

    @Override
    public ApprovalVO getApprovalDetail(Long id) {
        Approval approval = getById(id);
        if (approval == null) {
            throw new BusinessException("审批记录不存在");
        }
        return approvalMapper.selectApprovalVO(id);
    }

    @Override
    public List<ApprovalVO> getApprovalHistory(Long appointmentId) {
        QueryWrapper<Approval> wrapper = new QueryWrapper<>();
        wrapper.eq("appointment_id", appointmentId)
                .orderByAsc("create_time");

        List<Approval> approvals = list(wrapper);

        return approvals.stream()
                .map(a -> approvalMapper.selectApprovalVO(a.getId()))
                .collect(Collectors.toList());
    }
}
