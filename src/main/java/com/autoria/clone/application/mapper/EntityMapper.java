package com.autoria.clone.application.mapper;

import com.autoria.clone.application.dto.AdvertisementDTO;
import com.autoria.clone.application.dto.DealershipDTO;
import com.autoria.clone.application.dto.UserDTO;
import com.autoria.clone.application.dto.ViewLogDTO;
import com.autoria.clone.domain.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EntityMapper {


    public AdvertisementDTO toAdvertisementDTO(Advertisement advertisement) {
        AdvertisementDTO dto = new AdvertisementDTO();
        dto.setId(advertisement.getId());
        dto.setCarBrand(advertisement.getCarBrand());
        dto.setCarModel(advertisement.getCarModel());
        dto.setPrice(advertisement.getPrice());
        dto.setOriginalCurrency(advertisement.getOriginalCurrency());
        dto.setCity(advertisement.getCity());
        dto.setRegion(advertisement.getRegion());
        dto.setDescription(advertisement.getDescription());
        dto.setStatus(advertisement.getStatus());
        dto.setEditAttempts(advertisement.getEditAttempts());
        dto.setConvertedPrices(advertisement.getConvertedPrices());
        dto.setViews(advertisement.getViews());
        dto.setExchangeRateSource(advertisement.getExchangeRateSource());
        dto.setUserId(advertisement.getUser() != null ? advertisement.getUser().getId() : null);
        dto.setDealershipId(advertisement.getDealership() != null ? advertisement.getDealership().getId() : null);
        return dto;
    }

    public Advertisement toAdvertisementEntity(AdvertisementDTO dto, User user, Dealership dealership) {
        Advertisement advertisement = new Advertisement();
        advertisement.setId(dto.getId());
        advertisement.setCarBrand(dto.getCarBrand());
        advertisement.setCarModel(dto.getCarModel());
        advertisement.setPrice(dto.getPrice());
        advertisement.setOriginalCurrency(dto.getOriginalCurrency());
        advertisement.setCity(dto.getCity());
        advertisement.setRegion(dto.getRegion());
        advertisement.setDescription(dto.getDescription());
        advertisement.setStatus(dto.getStatus());
        advertisement.setEditAttempts(dto.getEditAttempts());
        advertisement.setConvertedPrices(dto.getConvertedPrices());
        advertisement.setViews(dto.getViews());
        advertisement.setExchangeRateSource(dto.getExchangeRateSource());
        advertisement.setUser(user);
        advertisement.setDealership(dealership);
        return advertisement;
    }


    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPremium(user.isPremium());
        dto.setAdvertisementCount(user.getAdvertisementCount());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return dto;
    }

    public User toUserEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPremium(dto.isPremium());
        user.setAdvertisementCount(dto.getAdvertisementCount());
        return user;
    }


    public DealershipDTO toDealershipDTO(Dealership dealership) {
        DealershipDTO dto = new DealershipDTO();
        dto.setId(dealership.getId());
        dto.setName(dealership.getName());
        dto.setAddress(dealership.getAddress());
        dto.setUserIds(dealership.getUsers().stream().map(User::getId).collect(Collectors.toList()));
        dto.setUserRoles(dealership.getUserRoles());
        return dto;
    }

    public Dealership toDealershipEntity(DealershipDTO dto) {
        Dealership dealership = new Dealership();
        dealership.setId(dto.getId());
        dealership.setName(dto.getName());
        dealership.setAddress(dto.getAddress());
        return dealership;
    }

    public ViewLogDTO toViewLogDTO(ViewLog viewLog) {
        ViewLogDTO dto = new ViewLogDTO();
        dto.setId(viewLog.getId());
        dto.setAdvertisementId(viewLog.getAdvertisement().getId());
        dto.setViewDate(viewLog.getViewDate());
        return dto;
    }

    public ViewLog toViewLogEntity(ViewLogDTO dto, Advertisement advertisement) {
        ViewLog viewLog = new ViewLog();
        viewLog.setId(dto.getId());
        viewLog.setAdvertisement(advertisement);
        viewLog.setViewDate(dto.getViewDate());
        return viewLog;
    }
}