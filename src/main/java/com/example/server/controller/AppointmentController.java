package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.dto.AppointmentDTO;
import com.example.server.entity.Appointment;
import com.example.server.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约Controller
 */
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 创建预约
     */
    @PostMapping("/create")
    public Result<Long> createAppointment(@RequestBody AppointmentDTO dto) {
        Long id = appointmentService.createAppointment(dto);
        return Result.success("预约创建成功", id);
    }

    /**
     * 查询预约详情
     */
    @GetMapping("/{id}")
    public Result<Appointment> getAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        return Result.success(appointment);
    }

    /**
     * 查询预约列表
     */
    @GetMapping("/list")
    public Result<List<Appointment>> listAppointments(
            @RequestParam(required = false) Long visitorId,
            @RequestParam(required = false) String status) {
        List<Appointment> appointments = appointmentService.listAppointments(visitorId, status);
        return Result.success(appointments);
    }

    /**
     * 取消预约
     */
    @PutMapping("/cancel/{id}")
    public Result<String> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return Result.success("预约已取消");
    }
}
