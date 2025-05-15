package com.autoria.clone.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UserDetailsService userDetailsService;
    private final Key jwtSigningKey;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, @Value("${jwt.secret}") String jwtSecret) {
        this.userDetailsService = userDetailsService;
        this.jwtSigningKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/api/auth/register") || path.equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSigningKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String rolesStr = (String) claims.get("roles");
            String permissionsStr = (String) claims.get("permissions");

            logger.debug("Token claims - username: {}, roles: {}, permissions: {}", username, rolesStr, permissionsStr);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (rolesStr != null && !rolesStr.trim().isEmpty()) {
                    authorities.addAll(Arrays.stream(rolesStr.split(","))
                            .map(String::trim)
                            .filter(role -> !role.isEmpty())
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList()));
                }
                if (permissionsStr != null && !permissionsStr.trim().isEmpty()) {
                    authorities.addAll(Arrays.stream(permissionsStr.split(","))
                            .map(String::trim)
                            .filter(perm -> !perm.isEmpty())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));
                }

                logger.debug("Authorities after processing: {}", authorities);

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        username, "", authorities
                );

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                request.setAttribute("user", userDetails);
            }
        } catch (Exception e) {
            logger.error("JWT processing error: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}