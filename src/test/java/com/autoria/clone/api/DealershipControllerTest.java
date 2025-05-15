package com.autoria.clone.api;

import com.autoria.clone.application.dto.DealershipDTO;
import com.autoria.clone.application.mapper.EntityMapper;
import com.autoria.clone.application.service.DealershipService;
import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealershipController.class)
public class DealershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealershipService dealershipService;

    @MockBean
    private EntityMapper entityMapper;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDealershipSuccess() throws Exception {
        DealershipDTO dealershipDTO = new DealershipDTO();
        dealershipDTO.setName("AutoSalon");
        dealershipDTO.setUserIds(Collections.singletonList(1L));

        Dealership dealership = new Dealership();
        User user = new User();

        when(entityMapper.toDealershipEntity(any(DealershipDTO.class))).thenReturn(dealership);
        when(dealershipService.createDealership(any(Dealership.class), anyLong())).thenReturn(dealership);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(entityMapper.toDealershipDTO(dealership)).thenReturn(dealershipDTO);

        mockMvc.perform(post("/dealerships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"AutoSalon\",\"address\":\"Kyiv\",\"userIds\":[1]}")
                        .param("adminId", "1"))
                .andExpect(status().isOk());
    }
}