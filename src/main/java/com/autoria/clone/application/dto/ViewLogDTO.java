package com.autoria.clone.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewLogDTO {
    private Long id;
    private Long advertisementId;
    private LocalDateTime viewDate;
    private LocalDateTime timestamp;
}