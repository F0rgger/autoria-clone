package com.autoria.clone.api;

import com.autoria.clone.application.dto.AdvertisementDTO;
import com.autoria.clone.application.dto.ContactRequestDTO;
import com.autoria.clone.application.mapper.EntityMapper;
import com.autoria.clone.application.service.AdvertisementService;
import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import com.autoria.clone.domain.repository.DealershipRepository;
import com.autoria.clone.domain.repository.UserRepository;
import com.autoria.clone.infrastructure.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdvertisementController.class)
public class AdvertisementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvertisementService advertisementService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EntityMapper entityMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DealershipRepository dealershipRepository;

    private Advertisement advertisement;
    private AdvertisementDTO advertisementDTO;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1L);
        advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setUser(user);
        advertisement.setCarBrand(CarBrand.BMW);
        advertisement.setCarModel(CarModel.X5);
        advertisement.setPrice(new BigDecimal("35000"));
        advertisement.setOriginalCurrency("USD");
        advertisement.setCity("Kyiv");
        advertisement.setRegion("Kyiv");
        advertisement.setDescription("Good car");

        advertisementDTO = new AdvertisementDTO();
        advertisementDTO.setId(1L);
        advertisementDTO.setUserId(1L);
        advertisementDTO.setCarBrand(CarBrand.BMW);
        advertisementDTO.setCarModel(CarModel.X5);
        advertisementDTO.setPrice(new BigDecimal("35000"));
        advertisementDTO.setOriginalCurrency("USD");
        advertisementDTO.setCity("Kyiv");
        advertisementDTO.setRegion("Kyiv");
        advertisementDTO.setDescription("Good car");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(advertisementService.createAdvertisement(any(User.class), any(Advertisement.class))).thenReturn(advertisement);
        when(entityMapper.toAdvertisementDTO(advertisement)).thenReturn(advertisementDTO);
        when(advertisementService.getAdvertisementById(1L)).thenReturn(advertisement);
        when(advertisementService.searchAdvertisements(null, null, null, null, null, null, null, any())).thenReturn(new PageImpl<>(Collections.singletonList(advertisement)));
        when(entityMapper.toAdvertisementDTO(any(Advertisement.class))).thenReturn(advertisementDTO);
    }

    @Test
    @WithMockUser(authorities = "CREATE_ADVERTISEMENT")
    public void testCreateAdvertisementSuccess() throws Exception {
        mockMvc.perform(post("/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"carBrand\":\"BMW\",\"carModel\":\"X5\",\"price\":35000,\"originalCurrency\":\"USD\",\"city\":\"Kyiv\",\"region\":\"Kyiv\",\"description\":\"Good car\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(authorities = "VIEW_ADVERTISEMENT_STATS")
    public void testGetAdvertisementStatsSuccess() throws Exception {
        when(advertisementService.getAdvertisementStats(1L, any(User.class))).thenReturn(Collections.singletonMap("views", 10));

        mockMvc.perform(get("/advertisements/1/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.views").value(10));
    }

    @Test
    public void testSearchAdvertisements() throws Exception {
        Page<Advertisement> page = new PageImpl<>(Collections.singletonList(advertisement));
        when(advertisementService.searchAdvertisements(null, null, null, null, null, null, null, any())).thenReturn(page);

        mockMvc.perform(get("/advertisements/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testContactSeller() throws Exception {
        ContactRequestDTO contactRequest = new ContactRequestDTO();
        contactRequest.setMessage("Interested in your car");
        contactRequest.setContactInfo("user@example.com");

        when(advertisementService.getAdvertisementById(1L)).thenReturn(advertisement);

        mockMvc.perform(post("/advertisements/1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Interested in your car\",\"contactInfo\":\"user@example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "EDIT_ADVERTISEMENT")
    public void testEditAdvertisementSuccess() throws Exception {
        when(advertisementService.updateAdvertisement(1L, any(Advertisement.class))).thenReturn(advertisement);
        mockMvc.perform(put("/advertisements/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"carBrand\":\"BMW\",\"carModel\":\"X5\",\"price\":35000,\"originalCurrency\":\"USD\",\"city\":\"Kyiv\",\"region\":\"Kyiv\",\"description\":\"Updated car\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated car"));
    }

    @Test
    @WithMockUser(authorities = "EDIT_ADVERTISEMENT")
    public void testDeleteAdvertisementSuccess() throws Exception {
        mockMvc.perform(delete("/advertisements/1?userId=1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "EDIT_ADVERTISEMENT")
    public void testDeleteAdvertisementForbidden() throws Exception {
        doThrow(new SecurityException("User is not the owner of the advertisement")).when(advertisementService).deleteAdvertisement(1L, 2L);
        mockMvc.perform(delete("/advertisements/1?userId=2"))
                .andExpect(status().isForbidden());
    }
}