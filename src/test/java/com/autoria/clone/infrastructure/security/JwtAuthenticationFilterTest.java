package com.autoria.clone.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JwtAuthenticationFilterTest {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private HttpServletResponse response;

    @MockBean
    private FilterChain filterChain;

    private Key jwtSigningKey;

    @BeforeEach
    public void setUp() {
        jwtSigningKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode("your_secret_key"));
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternalValidJwt() throws ServletException, IOException {
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .claim("roles", "BUYER")
                .claim("permissions", "VIEW_ADS,CONTACT_SELLER")
                .signWith(jwtSigningKey, SignatureAlgorithm.HS512)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/protected");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(request).setAttribute("user", userDetails);
    }

    @Test
    public void testDoFilterInternalInvalidJwt() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(request.getServletPath()).thenReturn("/protected");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}