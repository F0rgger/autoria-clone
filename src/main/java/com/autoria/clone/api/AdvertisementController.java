package com.autoria.clone.api;

import com.autoria.clone.application.dto.AdvertisementDTO;
import com.autoria.clone.application.dto.ContactRequestDTO;
import com.autoria.clone.application.mapper.EntityMapper;
import com.autoria.clone.application.service.AdvertisementService;
import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import com.autoria.clone.domain.repository.DealershipRepository;
import com.autoria.clone.domain.repository.UserRepository;
import com.autoria.clone.infrastructure.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/advertisements")
@RequiredArgsConstructor
@Validated
public class AdvertisementController {

    private static final Logger logger = LoggerFactory.getLogger(AdvertisementController.class);

    private final AdvertisementService advertisementService;
    private final EmailService emailService;
    private final EntityMapper entityMapper;
    private final UserRepository userRepository;
    private final DealershipRepository dealershipRepository;


    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ADVERTISEMENT')")
    public ResponseEntity<AdvertisementDTO> createAdvertisement(@Valid @RequestBody AdvertisementDTO advertisementDTO) {
        logger.debug("Received AdvertisementDTO: {}", advertisementDTO);
        if (advertisementDTO.getUserId() == null) {
            logger.warn("UserId is null in createAdvertisement");
            return ResponseEntity.badRequest().body(null);
        }
        User user = userRepository.findById(advertisementDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + advertisementDTO.getUserId()));
        Dealership dealership = advertisementDTO.getDealershipId() != null
                ? dealershipRepository.findById(advertisementDTO.getDealershipId())
                .orElseThrow(() -> new IllegalArgumentException("Dealership not found with ID: " + advertisementDTO.getDealershipId()))
                : null;

        Advertisement advertisement = entityMapper.toAdvertisementEntity(advertisementDTO, user, dealership);
        Advertisement created = advertisementService.createAdvertisement(user, advertisement);
        return ResponseEntity.ok(entityMapper.toAdvertisementDTO(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_ADVERTISEMENT')")
    public ResponseEntity<AdvertisementDTO> editAdvertisement(@PathVariable Long id, @Valid @RequestBody AdvertisementDTO advertisementDTO) {
        if (advertisementDTO.getUserId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Advertisement updated = advertisementService.updateAdvertisement(id, entityMapper.toAdvertisementEntity(
                advertisementDTO,
                userRepository.findById(advertisementDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + advertisementDTO.getUserId())),
                advertisementDTO.getDealershipId() != null ? dealershipRepository.findById(advertisementDTO.getDealershipId()).orElseThrow(() -> new IllegalArgumentException("Dealership not found with ID: " + advertisementDTO.getDealershipId())) : null));
        return ResponseEntity.ok(entityMapper.toAdvertisementDTO(updated));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AdvertisementDTO>> searchAdvertisements(
            @RequestParam(required = false) String carBrand,
            @RequestParam(required = false) String carModel,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String currency,
            Pageable pageable) {
        logger.debug("Searching advertisements with params: carBrand={}, carModel={}, minPrice={}, maxPrice={}, city={}, region={}, currency={}",
                carBrand, carModel, minPrice, maxPrice, city, region, currency);


        CarBrand carBrandEnum = null;
        if (carBrand != null && !carBrand.isEmpty()) {
            try {
                carBrandEnum = CarBrand.valueOf(carBrand.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid carBrand value: {}. Valid values are: {}", carBrand, Arrays.toString(CarBrand.values()));
                return ResponseEntity.badRequest().body(null);
            }
        }


        CarModel carModelEnum = null;
        if (carModel != null && !carModel.isEmpty()) {
            try {
                carModelEnum = CarModel.valueOf(carModel.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid carModel value: {}. Valid values are: {}", carModel, Arrays.toString(CarModel.values()));
                return ResponseEntity.badRequest().body(null);
            }
        }

        Page<Advertisement> result = advertisementService.searchAdvertisements(
                carBrandEnum, carModelEnum, minPrice, maxPrice, city, region, currency, pageable);
        Page<AdvertisementDTO> dtoPage = new PageImpl<>(
                result.getContent().stream().map(entityMapper::toAdvertisementDTO).collect(Collectors.toList()),
                pageable,
                result.getTotalElements());
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping("/{id}/contact")
    public ResponseEntity<Void> contactSeller(@PathVariable Long id, @Valid @RequestBody ContactRequestDTO contactRequest) {
        Advertisement ad = advertisementService.getAdvertisementById(id);
        String recipient = ad.getDealership() != null ? ad.getDealership().getUsers().get(0).getEmail() : ad.getUser().getEmail();
        String subject = "Запит щодо оголошення #" + id;
        String body = "<h2>Повідомлення від покупця</h2><p>" + contactRequest.getMessage() + "</p><p>Контакт: " + contactRequest.getContactInfo() + "</p>";
        emailService.sendEmail(recipient, subject, body);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("hasAuthority('VIEW_ADVERTISEMENT_STATS')")
    public ResponseEntity<Map<String, Object>> getAdvertisementStats(
            @PathVariable Long id,
            @RequestAttribute("user") UserDetails userDetails) {

        com.autoria.clone.domain.entity.User user = advertisementService.loadUserEntity(userDetails.getUsername());
        Map<String, Object> stats = advertisementService.getAdvertisementStats(id, user);
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_ADVERTISEMENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userDetails.getUsername()));
        advertisementService.deleteAdvertisement(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}