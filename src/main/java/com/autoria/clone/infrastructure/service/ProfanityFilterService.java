package com.autoria.clone.infrastructure.service;

import com.autoria.clone.infrastructure.exception.ProfanityException;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProfanityFilterService {

    private static final Logger logger = LoggerFactory.getLogger(ProfanityFilterService.class);
    private final LanguageServiceClient languageClient;

    public ProfanityFilterService(LanguageServiceClient languageClient) {
        this.languageClient = languageClient;
    }

    public boolean containsProfanity(String text) {
        logger.debug("Проверка текста на нецензурную лексику: {}", text);
        try {
            Document document = Document.newBuilder()
                    .setContent(text)
                    .setType(Type.PLAIN_TEXT)
                    .build();

            Sentiment sentiment = languageClient.analyzeSentiment(document).getDocumentSentiment();
            float score = sentiment.getScore();


            boolean containsProfanity = score < -0.5;
            logger.debug("Оценка тональности: {}, содержит нецензурную лексику: {}", score, containsProfanity);

            if (containsProfanity) {
                throw new ProfanityException("Текст может содержать нецензурную лексику на основе анализа тональности: " + text);
            }
            return false;
        } catch (Exception e) {
            logger.error("Ошибка при вызове Google Cloud NLP API: {}", e.getMessage(), e);
            throw new ProfanityException("Ошибка при вызове Google Cloud NLP API: " + e.getMessage(), e);
        }
    }
}