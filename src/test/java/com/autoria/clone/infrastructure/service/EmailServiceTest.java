package com.autoria.clone.infrastructure.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void testSendEmailSuccess() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail("recipient@example.com", "Subject", "Body");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendEmailFailure() throws MessagingException {
        when(mailSender.createMimeMessage()).thenThrow(new MessagingException("Failed to send email"));

        assertThrows(RuntimeException.class, () -> {
            emailService.sendEmail("recipient@example.com", "Subject", "Body");
        });
    }
}