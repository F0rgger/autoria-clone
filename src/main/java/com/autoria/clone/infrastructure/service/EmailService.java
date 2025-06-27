

package com.autoria.clone.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String body) {

        logger.info("Mocked email sent to: {}\nSubject: {}\nBody: {}", to, subject, body);
    }
}