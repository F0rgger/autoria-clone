package com.autoria.clone.infrastructure.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProfanityFilterServiceIntegrationTest {

    private WireMockServer wireMockServer;

    @Autowired
    private ProfanityFilterService profanityFilterService;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        configureFor("localhost", 8089);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void containsProfanity_shouldDetectProfanity() {
        stubFor(post(urlEqualTo("/v1/documents:moderateText?key=test-api-key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"moderationCategories\": [{\"name\": \"Profanity\", \"confidence\": 0.8}]}")));

        boolean result = profanityFilterService.containsProfanity("This is a damn bad car");
        assertTrue(result);
    }
}