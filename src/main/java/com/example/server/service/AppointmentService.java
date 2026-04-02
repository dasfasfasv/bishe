package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.dto.AppointmentDTO;
import com.example.server.entity.Appointment;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预约Service接口
 */

public interface AppointmentService extends IService<Appointment> {
    /**
     * 创建预约
     */
    Long createAppointment(AppointmentDTO dto);

    /**
     * 查询预约详情
     */
    Appointment getAppointmentById(Long id);

    /**
     * 查询预约列表
     */
    List<Appointment> listAppointments(Long visitorId, String status);

    /**
     * 取消预约
     */
    void cancelAppointment(Long id);

    /**
     * 检查黑名单
     */
    boolean checkBlacklist(Long visitorId);

    /**
     * 检查预约冲突
     */
    boolean checkConflict(AppointmentDTO dto);
}
