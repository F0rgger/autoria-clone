package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.repository.AdvertisementRepository;
import com.autoria.clone.infrastructure.service.EmailService;
import com.autoria.clone.infrastructure.service.ProfanityFilterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервіс для модерації оголошень.
 */
@Service
@RequiredArgsConstructor
public class ModerationService {

    private static final Logger logger = LoggerFactory.getLogger(ModerationService.class);

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
        logger.debug("Checking advertisement with ID: {}", advertisementId);
        if (advertisementId == null) {
            throw new IllegalArgumentException("Advertisement ID must not be null");
        }

        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new IllegalArgumentException("Оголошення з ID " + advertisementId + " не знайдено"));

        boolean containsProfanity = profanityFilterService.containsProfanity(advertisement.getDescription());
        logger.debug("Profanity check result for advertisement ID {}: {}", advertisementId, containsProfanity);

        if (!containsProfanity) {
            advertisement.setStatus("ACTIVE");
            advertisement.setEditAttempts(0);
            logger.debug("Advertisement ID {} passed moderation, status set to ACTIVE", advertisementId);
            advertisementRepository.save(advertisement);
            return true;
        }

        int attempts = advertisement.getEditAttempts() + 1;
        advertisement.setEditAttempts(attempts);
        logger.debug("Advertisement ID {} failed moderation, edit attempts: {}", advertisementId, attempts);

        if (attempts >= 3) {
            advertisement.setStatus("INACTIVE");
            logger.debug("Advertisement ID {} set to INACTIVE due to max edit attempts", advertisementId);
            notifyManager(advertisement);
        } else {
            advertisement.setStatus("PENDING");
            logger.debug("Advertisement ID {} set to PENDING", advertisementId);
        }

        advertisementRepository.save(advertisement);
        return false;
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
        logger.debug("Notified manager about advertisement ID: {}", advertisement.getId());
    }
}