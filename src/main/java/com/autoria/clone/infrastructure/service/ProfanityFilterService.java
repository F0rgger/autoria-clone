package com.autoria.clone.infrastructure.service;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.ModerateTextResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Сервіс для фільтрації нецензурної лексики за допомогою Google Cloud Natural Language API.
 */
@Service
@RequiredArgsConstructor
public class ProfanityFilterService {

    @Value("${google.cloud.nlp.api.key}")
    private String apiKey;

    /**
     * Перевіряє текст на наявність нецензурної лексики.
     *
     * @param text Текст для перевірки
     * @return true, якщо текст містить нецензурну лексику; false — якщо текст чистий
     */
    public boolean containsProfanity(String text) {
        try (LanguageServiceClient languageClient = LanguageServiceClient.create()) {
            Document document = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            ModerateTextResponse response = languageClient.moderateText(document);

            return response.getModerationCategoriesList().stream()
                    .anyMatch(category ->
                            category.getName().matches("Toxic|Insult|Profanity") &&
                                    category.getConfidence() > 0.5);
        } catch (IOException e) {
            throw new RuntimeException("Помилка при виклику Google Cloud NLP API: " + e.getMessage(), e);
        }
    }
}