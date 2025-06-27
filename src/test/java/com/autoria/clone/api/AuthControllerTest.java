package com.autoria.clone.api;

import com.autoria.clone.application.dto.UserDTO;
import com.autoria.clone.application.mapper.EntityMapper;
import com.autoria.clone.domain.entity.Role;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.RoleRepository;
import com.autoria.clone.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EntityMapper entityMapper;

    private User user;
    private UserDTO userDTO;
    private Role buyerRole;

    @BeforeEach
    public void setUp() {
        userDTO = new UserDTO();
        userDTO.setEmail("user@example.com");
        userDTO.setPassword("password");

        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setAdvertisementCount(0);

        buyerRole = new Role();
        buyerRole.setName(Role.BUYER);
        buyerRole.setPermissions(Arrays.asList("VIEW_ADS", "CONTACT_SELLER"));

        when(entityMapper.toUserEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(Role.BUYER)).thenReturn(Optional.of(buyerRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь зарегистрирован"));
    }

    @Test
    public void testRegisterUserAlreadyExists() throws Exception {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with email user@example.com already exists"));
    }

    @Test
    public void testAdvertisementCountOnRegister() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk());

        assert user.getAdvertisementCount() == 0;
    }

    @Test
    public void testLoginSuccess() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(entityMapper.toUserEntity(any(UserDTO.class))).thenReturn(user);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginInvalidPassword() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpgradeAccount() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(roleRepository.findByName("SELLER")).thenReturn(Optional.of(new Role()));
        mockMvc.perform(post("/api/auth/upgrade?email=user@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateManager() throws Exception {
        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("MANAGER")).thenReturn(Optional.of(new Role()));
        mockMvc.perform(post("/api/auth/create-manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"manager@example.com\",\"password\":\"manager123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setId(1L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        mockMvc.perform(post("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user@example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"password\"}"))
                .andExpect(status().isBadRequest());
    }
}