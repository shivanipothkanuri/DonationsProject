package org.charityaid.charity_aid.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.charityaid.charity_aid.dto.DonationResponse;
import org.charityaid.charity_aid.security.JwtAuthFilter;
import org.charityaid.charity_aid.service.DonationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DonationController.class)
@AutoConfigureMockMvc(addFilters = false)
class DonationControllerBehaviorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonationService donationService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void getDonation_withStaffRole_returnsOk() throws Exception {
        when(donationService.getDonationById(1)).thenReturn(DonationResponse.builder().donationId(1).build());

        mockMvc.perform(get("/api/donations/1")
                        .with(user("staff@example.com").roles("STAFF")))
          .andExpect(status().isOk());

        verify(donationService).getDonationById(1);
    }

    @Test
          void getDonation_withDonorRole_stillCallsControllerPath() throws Exception {
        when(donationService.getDonationById(2)).thenReturn(DonationResponse.builder().donationId(2).build());

        mockMvc.perform(get("/api/donations/2")
                        .with(user("donor@example.com").roles("DONOR")))
          .andExpect(status().isOk());

        verify(donationService).getDonationById(2);
    }

  @Test
  void getInKindOptions_returnsOkAndCallsService() throws Exception {
    when(donationService.getInKindDonationOptions()).thenReturn(java.util.List.of("Rice", "Blankets"));

    mockMvc.perform(get("/api/donations/in-kind-options")
            .with(user("donor@example.com").roles("DONOR")))
        .andExpect(status().isOk());

    verify(donationService).getInKindDonationOptions();
  }
}
