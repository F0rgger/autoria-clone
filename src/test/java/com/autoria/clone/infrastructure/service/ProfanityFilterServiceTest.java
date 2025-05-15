package com.autoria.clone.infrastructure.service;

import com.autoria.clone.infrastructure.exception.ProfanityException;
import com.google.cloud.language.v1.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProfanityFilterServiceTest {

    @Autowired
    private ProfanityFilterService profanityFilterService;

    private LanguageServiceClient languageClient;

    @BeforeEach
    public void setUp() {
        languageClient = mock(LanguageServiceClient.class);
        ReflectionTestUtils.setField(profanityFilterService, "apiKey", "dummy-key");
    }

    @Test
    public void testContainsProfanityTrue() throws IOException {
        Document document = Document.newBuilder().setContent("bad word").setType(Document.Type.PLAIN_TEXT).build();
        ModerateTextResponse response = ModerateTextResponse.newBuilder()
                .addModerationCategories(ClassificationCategory.newBuilder()
                        .setName("Profanity")
                        .setConfidence(0.6f))
                .build();

        when(languageClient.moderateText(document)).thenReturn(response);

        assertThrows(ProfanityException.class, () -> {
            profanityFilterService.containsProfanity("bad word");
        });
    }

    @Test
    public void testContainsProfanityFalse() throws IOException {
        Document document = Document.newBuilder().setContent("clean text").setType(Document.Type.PLAIN_TEXT).build();
        ModerateTextResponse response = ModerateTextResponse.newBuilder()
                .addModerationCategories(ClassificationCategory.newBuilder()
                        .setName("Safe")
                        .setConfidence(0.1f))
                .build();

        when(languageClient.moderateText(document)).thenReturn(response);

        boolean result = profanityFilterService.containsProfanity("clean text");
        assertFalse(result);
    }

    @Test
    public void testContainsProfanityIOException() throws IOException {
        Document document = Document.newBuilder().setContent("text").setType(Document.Type.PLAIN_TEXT).build();
        when(languageClient.moderateText(document)).thenThrow(new IOException("API error"));

        assertThrows(ProfanityException.class, () -> {
            profanityFilterService.containsProfanity("text");
        });
    }
}