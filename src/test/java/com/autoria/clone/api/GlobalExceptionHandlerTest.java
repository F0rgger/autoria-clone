package com.autoria.clone.api;

import com.autoria.clone.infrastructure.exception.ProfanityException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestController testController;

    @Test
    public void testHandleAccessDeniedException() throws Exception {
        when(testController.protectedEndpoint()).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/test/protected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: Access denied"));
    }

    @Test
    public void testHandleDataIntegrityViolationException() throws Exception {
        when(testController.protectedEndpoint()).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        mockMvc.perform(get("/api/test/protected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("Database error: Duplicate entry or constraint violation"));
    }

    @Test
    public void testHandleJwtException() throws Exception {
        when(testController.protectedEndpoint()).thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(get("/api/test/protected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("JWT processing error: Invalid token"));
    }

    @Test
    public void testHandleProfanityException() throws Exception {
        when(testController.protectedEndpoint()).thenThrow(new ProfanityException("Profanity detected"));

        mockMvc.perform(get("/api/test/protected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Profanity detected, please edit your advertisement: Profanity detected"));
    }

    @Test
    public void testHandleGenericException() throws Exception {
        when(testController.protectedEndpoint()).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/test/protected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error: Unexpected error"));
    }
}