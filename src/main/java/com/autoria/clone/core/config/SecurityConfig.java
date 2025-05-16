package com.autoria.clone.core.config;

import com.autoria.clone.infrastructure.security.JwtAuthenticationFilter;
import com.autoria.clone.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/upgrade").hasRole("BUYER")
                        .requestMatchers("/api/auth/create-manager").hasRole("ADMIN")
                        .requestMatchers("/advertisements/search").permitAll()
                        .requestMatchers("/advertisements").hasAuthority("CREATE_ADVERTISEMENT")
                        .requestMatchers("/advertisements/*/edit").hasAuthority("EDIT_ADVERTISEMENT")
                        .requestMatchers("/advertisements/*/stats").hasAuthority("VIEW_ADVERTISEMENT_STATS")
                        .requestMatchers("/advertisements/*/contact").permitAll()
                        .requestMatchers("/dealerships/**").hasRole("ADMIN")
                        .requestMatchers("/api/cars/report-missing-brand").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Transactional(readOnly = true)
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .map(user -> {

                    user.getRoles().forEach(role -> role.getPermissions().size());
                    var authorities = user.getRoles().stream()
                            .flatMap(role -> {
                                var roleAuthority = new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName());
                                var permissionAuthorities = role.getPermissions().stream()
                                        .map(permission -> new org.springframework.security.core.authority.SimpleGrantedAuthority(permission))
                                        .collect(Collectors.toList());
                                permissionAuthorities.add(roleAuthority);
                                return permissionAuthorities.stream();
                            })
                            .collect(Collectors.toList());
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            user.getPassword(),
                            authorities);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}