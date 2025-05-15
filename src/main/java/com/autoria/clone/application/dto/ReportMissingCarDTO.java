package com.autoria.clone.application.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReportMissingCarDTO {
    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    private String message;
}