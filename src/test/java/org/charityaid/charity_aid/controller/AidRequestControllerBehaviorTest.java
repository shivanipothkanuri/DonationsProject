package org.charityaid.charity_aid.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.charityaid.charity_aid.dto.AidRequestResponse;
import org.charityaid.charity_aid.security.JwtAuthFilter;
import org.charityaid.charity_aid.service.AidRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AidRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AidRequestControllerBehaviorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AidRequestService aidRequestService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
        void getRequests_returnsOkAndCallsService() throws Exception {
                Page<AidRequestResponse> page = new PageImpl<>(java.util.List.of());
                when(aidRequestService.getAllRequests(any())).thenReturn(page);

                mockMvc.perform(get("/api/aid-requests")
                        .with(user("staff@example.com").roles("STAFF"))
                                                .param("page", "0")
                                                .param("size", "10"))
                                .andExpect(status().isOk());

                verify(aidRequestService).getAllRequests(any());
    }

    @Test
        void getRequestById_returnsOkAndCallsService() throws Exception {
                when(aidRequestService.getRequestById(1)).thenReturn(AidRequestResponse.builder().requestId(1).build());

                mockMvc.perform(get("/api/aid-requests/1")
                        .with(user("staff@example.com").roles("STAFF"))
                                                .param("page", "0")
                                                .param("size", "10"))
                                .andExpect(status().isOk());

                verify(aidRequestService).getRequestById(1);
    }

    @Test
        void getRequestsByBeneficiary_returnsOkAndCallsService() throws Exception {
                Page<AidRequestResponse> page = new PageImpl<>(java.util.List.of());
                when(aidRequestService.getRequestsByBeneficiary(eq(2), any())).thenReturn(page);

                mockMvc.perform(get("/api/aid-requests/beneficiary/2")
                                                .with(user("staff@example.com").roles("STAFF"))
                                                .param("page", "0")
                                                .param("size", "10"))
                .andExpect(status().isOk());

                verify(aidRequestService).getRequestsByBeneficiary(eq(2), any());
    }
}
