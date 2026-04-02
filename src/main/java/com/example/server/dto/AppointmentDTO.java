package com.example.server.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long visitorId;
    private String visitorName;
    private String visitorPhone;
    private Long departmentId;
    private String visitReason;
    private LocalDateTime appointmentTime;
    private LocalDateTime appointmentEndTime;
}
