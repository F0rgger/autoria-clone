package com.autoria.clone.api;

import com.autoria.clone.application.dto.ReportMissingCarDTO;
import com.autoria.clone.application.service.CarService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);
    private final CarService carService;

    @PostMapping("/report-missing-brand")
    public ResponseEntity<String> reportMissingBrand(@Valid @RequestBody ReportMissingCarDTO report) {
        logger.info("Received report for missing car brand/model: {}/{}", report.getBrand(), report.getModel());
        carService.reportMissingCar(report);
        return ResponseEntity.ok("Report submitted successfully. The administration has been notified.");
    }
}