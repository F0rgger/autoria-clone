package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.DealershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервіс для управління автосалонами.
 */
@Service
@RequiredArgsConstructor
public class DealershipService {

    private final DealershipRepository dealershipRepository;

    /**
     * Створює новий автосалон.
     *
     * @param dealership Автосалон
     * @param admin Користувач-адміністратор автосалону
     * @return Створений автосалон
     */
    @Transactional
    public Dealership createDealership(Dealership dealership, User admin) {
        dealership.getUsers().add(admin);
        return dealershipRepository.save(dealership);
    }
}