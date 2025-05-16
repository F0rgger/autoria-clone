package com.autoria.clone.application.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class ContactRequestDTO {

    @NotBlank(message = "Повідомлення не може бути порожнім")
    private String message;

    @NotBlank(message = "Контактна інформація не може бути порожньою")
    private String contactInfo;
}