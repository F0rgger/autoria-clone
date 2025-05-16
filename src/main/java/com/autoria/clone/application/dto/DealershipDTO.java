package com.autoria.clone.application.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DealershipDTO {
    private Long adminId;
    private Long id;
    private String name;
    private String address;
    private List<Long> userIds;
    private Map<Long, String> userRoles;
}