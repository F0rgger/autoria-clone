package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.repository.AdvertisementRepository;
import com.autoria.clone.infrastructure.service.EmailService;
import com.autoria.clone.infrastructure.service.ProfanityFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервіс для модерації оголошень.
 */
@Service
@RequiredArgsConstructor
public class ModerationService {

    private final AdvertisementRepository advertisementRepository;
    private final ProfanityFilterService profanityFilterService;
    private final EmailService emailService;

    /**
     * Перевіряє оголошення на нецензурну лексику.
     *
     * @param advertisementId ID оголошення
     * @return true, якщо оголошення пройшло перевірку; false, якщо містить нецензурну лексику
     */
    @Transactional
    public boolean checkAdvertisement(Long advertisementId) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new IllegalArgumentException("Оголошення з ID " + advertisementId + " не знайдено"));

        boolean containsProfanity = profanityFilterService.containsProfanity(advertisement.getDescription());

        if (!containsProfanity) {
            advertisement.setActive(true);
            advertisement.setEditAttempts(0);
            advertisementRepository.save(advertisement);
            return true;
        } else {
            int attempts = advertisement.getEditAttempts() + 1;
            advertisement.setEditAttempts(attempts);

            if (attempts >= 3) {
                advertisement.setActive(false);
                advertisementRepository.save(advertisement);
                notifyManager(advertisement);
                return false;
            }

            advertisementRepository.save(advertisement);
            return false;
        }
    }

    /**
     * Надсилає повідомлення менеджеру про оголошення, яке не пройшло перевірку.
     *
     * @param advertisement Оголошення
     */
    private void notifyManager(Advertisement advertisement) {
        String subject = "Оголошення потребує перевірки";
        String body = String.format(
                "<h2>Оголошення не пройшло перевірку</h2><p>ID: %d<br>Опис: %s<br>Кількість спроб редагування: %d</p>",
                advertisement.getId(), advertisement.getDescription(), advertisement.getEditAttempts());
        emailService.sendEmail("manager@autoria.clone", subject, body);
    }
}