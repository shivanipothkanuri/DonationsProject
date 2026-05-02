package org.charityaid.charity_aid.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.charityaid.charity_aid.dto.AuthResponse;
import org.charityaid.charity_aid.security.JwtAuthFilter;
import org.charityaid.charity_aid.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerBehaviorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void register_withValidPayload_returnsCreatedAndCallsService() throws Exception {
        AuthResponse response = AuthResponse.builder().token("jwt-token").userId(1).email("donor@example.com").build();
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Donor User",
                                  "email": "donor@example.com",
                                  "password": "password123",
                                  "phoneNumber": "+1 555 0100"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(authService).register(any());
    }

    @Test
    void register_withInvalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "",
                                  "email": "not-an-email",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withInvalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void setupNeeded_isPublicAndReturnsOk() throws Exception {
        when(authService.isSetupNeeded()).thenReturn(true);

        mockMvc.perform(get("/api/auth/setup-needed"))
          .andExpect(status().isOk());
    }
}
