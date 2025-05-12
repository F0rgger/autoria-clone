package com.autoria.clone.api;

import com.autoria.clone.application.dto.ContactRequestDTO;
import com.autoria.clone.application.service.AdvertisementService;
import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.infrastructure.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * Контролер для управління оголошеннями.
 */
@RestController
@RequestMapping("/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final EmailService emailService;

    /**
     * Створює нове оголошення.
     *
     * @param advertisement Оголошення
     * @return Створене оголошення
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ADVERTISEMENT')")
    public ResponseEntity<Advertisement> createAdvertisement(@Valid @RequestBody Advertisement advertisement) {
        Advertisement created = advertisementService.createAdvertisement(advertisement.getUser(), advertisement);
        return ResponseEntity.ok(created);
    }

    /**
     * Шукає оголошення за параметрами.
     *
     * @param carBrand Марка автомобіля
     * @param carModel Модель автомобіля
     * @param minPrice Мінімальна ціна
     * @param maxPrice Максимальна ціна
     * @param city Місто
     * @param region Регіон
     * @param currency Валюта
     * @param pageable Пагінація
     * @return Сторінка з оголошеннями
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Advertisement>> searchAdvertisements(
            @RequestParam(required = false) String carBrand,
            @RequestParam(required = false) String carModel,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String currency,
            Pageable pageable) {
        Page<Advertisement> result = advertisementService.searchAdvertisements(
                carBrand, carModel, minPrice, maxPrice, city, region, currency, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Надсилає повідомлення продавцю або автосалону.
     *
     * @param id ID оголошення
     * @param contactRequest Запит на зв’язок
     * @return OK, якщо повідомлення надіслано
     */
    @PostMapping("/{id}/contact")
    public ResponseEntity<Void> contactSeller(@PathVariable Long id, @Valid @RequestBody ContactRequestDTO contactRequest) {
        Advertisement ad = advertisementService.getAdvertisementById(id);
        String recipient = ad.getDealership() != null ? ad.getDealership().getUsers().get(0).getEmail() : ad.getUser().getEmail();
        String subject = "Запит щодо оголошення #" + id;
        String body = "<h2>Повідомлення від покупця</h2><p>" + contactRequest.getMessage() + "</p><p>Контакт: " + contactRequest.getContactInfo() + "</p>";
        emailService.sendEmail(recipient, subject, body);
        return ResponseEntity.ok().build();
    }
}