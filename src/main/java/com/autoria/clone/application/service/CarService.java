package com.autoria.clone.application.service;

import com.autoria.clone.application.dto.ReportMissingCarDTO;
import com.autoria.clone.infrastructure.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final EmailService emailService;

    public void reportMissingCar(ReportMissingCarDTO report) {
        String subject = "Report: Missing Car Brand/Model";
        String body = String.format(
                "<h2>New Report for Missing Car Brand/Model</h2>" +
                        "<p><strong>Brand:</strong> %s</p>" +
                        "<p><strong>Model:</strong> %s</p>" +
                        "<p><strong>Additional Message:</strong> %s</p>",
                report.getBrand(), report.getModel(), report.getMessage() != null ? report.getMessage() : "No additional message"
        );
        emailService.sendEmail("admin@autoria.clone", subject, body);
    }
}