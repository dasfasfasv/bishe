package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.dto.AppointmentDTO;
import com.example.server.entity.Appointment;
import com.example.server.exception.BusinessException;
import com.example.server.mapper.AppointmentMapper;
import com.example.server.service.AppointmentService;
import com.example.server.service.ApprovalService;
import com.example.server.service.BlacklistService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约 Service 实现类
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private ApprovalService approvalService;

    @Override
    @Transactional
    public Long createAppointment(AppointmentDTO dto) {
        // 检查黑名单
        if (checkBlacklist(dto.getVisitorId())) {
            throw new BusinessException("该访客已被加入黑名单，无法预约");
        }

        // 检查预约冲突
        if (checkConflict(dto)) {
            throw new BusinessException("预约时间存在冲突，请重新选择");
        }

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(dto, appointment);
        appointment.setStatus("1");  // 待审批
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        save(appointment);

        // 创建审批流程
        try {
            approvalService.createApprovalWorkflow(appointment.getId());
        } catch (Exception e) {
            // 如果审批流程创建失败，继续保存预约
            System.err.println("创建审批流程失败: " + e.getMessage());
        }

        return appointment.getId();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return getById(id);
    }

    @Override
    public List<Appointment> listAppointments(Long visitorId, String status) {
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
        if (visitorId != null) {
            wrapper.eq("visitor_id", visitorId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        return list(wrapper);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        if ("2".equals(appointment.getStatus()) || "4".equals(appointment.getStatus())) {
            throw new BusinessException("已批准或已使用的预约无法取消");
        }
        appointment.setStatus("6");  // 已取消
        appointment.setUpdateTime(LocalDateTime.now());
        updateById(appointment);
    }

    @Override
    public boolean checkBlacklist(Long visitorId) {
        return blacklistService.isInBlacklist(visitorId);
    }

    @Override
    public boolean checkConflict(AppointmentDTO dto) {
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
        wrapper.eq("visitor_id", dto.getVisitorId())
                .in("status", "1", "2")  // 待审批、已批准
                .le("appointment_time", dto.getAppointmentEndTime())
                .ge("appointment_end_time", dto.getAppointmentTime());

        return count(wrapper) > 0;
    }
}