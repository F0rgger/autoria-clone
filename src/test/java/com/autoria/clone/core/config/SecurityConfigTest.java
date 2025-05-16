package com.autoria.clone.core.config;

import com.autoria.clone.domain.entity.Role;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;

@WebMvcTest
@ContextConfiguration(classes = {SecurityConfig.class})
public class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(authorities = "VIEW_ADS")
    public void testSecurityFilterChain() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        Role role = new Role();
        role.setName("BUYER");
        role.setPermissions(Collections.singletonList("VIEW_ADS"));
        user.setRoles(Collections.singletonList(role));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));


    }
}