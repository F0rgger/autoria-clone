package com.autoria.clone.infrastructure.service;

import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleCloudConfig {

    @Bean
    public LanguageServiceClient languageServiceClient() throws IOException {

        String credentialsPath = "C:\\Users\\maxfo\\IdeaProjects\\autoria-clone\\google-credentials.json";
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));


        String endpoint = "language.googleapis.com:443";
        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .setEndpoint(endpoint)
                .build();

        return LanguageServiceClient.create(settings);
    }
}