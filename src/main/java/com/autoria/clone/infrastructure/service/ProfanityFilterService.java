package com.autoria.clone.infrastructure.service;

import com.autoria.clone.infrastructure.exception.ProfanityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class ProfanityFilterService {
    private static final Logger logger = LoggerFactory.getLogger(ProfanityFilterService.class);

    // Простой список запрещённых слов (можно расширить)
    private static final Set<String> BAD_WORDS = new HashSet<>(Arrays.asList(
            "BAD_WORDS1", "BAD_WORDS2", "BAD_WORDS3", "BAD_WORDS4", "BAD_WORDS5", "BAD_WORDS6", "BAD_WORDS7", "BAD_WORDS8", "BAD_WORDS9", "BAD_WORDS10"
    ));

    public boolean containsProfanity(String text) {
        logger.debug("Проверка текста на нецензурную лексику: {}", text);
        String lower = text.toLowerCase(Locale.ROOT);
        for (String bad : BAD_WORDS) {
            if (lower.contains(bad)) {
                logger.debug("Обнаружено нецензурное слово: {}", bad);
                throw new ProfanityException("Текст содержит нецензурную лексику: '" + bad + "'");
            }
        }
        return false;
    }

}