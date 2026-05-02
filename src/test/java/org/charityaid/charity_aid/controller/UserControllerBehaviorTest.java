package org.charityaid.charity_aid.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.charityaid.charity_aid.dto.UserResponse;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.security.JwtAuthFilter;
import org.charityaid.charity_aid.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerBehaviorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void listUsers_returnsOkAndCallsService() throws Exception {
        Page<UserResponse> emptyPage = new PageImpl<>(java.util.List.of());
        when(userService.getAllUsers(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/users")
                        .with(user("staff@example.com").roles("STAFF")))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(any());
    }

    @Test
    void changeRole_withAdminRole_callsServiceAndReturnsOk() throws Exception {
        UserResponse response = UserResponse.builder()
                .userId(10)
                .fullName("Updated User")
                .emailAddress("user@example.com")
                .userRole(UserRole.STAFF)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        when(userService.changeRole(10, UserRole.STAFF)).thenReturn(response);

        mockMvc.perform(patch("/api/users/10/role")
                        .param("role", "STAFF")
                        .with(user("admin@example.com").roles("ADMINISTRATOR")))
            .andExpect(status().isOk());

        verify(userService).changeRole(eq(10), eq(UserRole.STAFF));
    }

    @Test
    void getUserById_withAdminRole_returnsOk() throws Exception {
        UserResponse response = UserResponse.builder()
                .userId(5)
                .fullName("Test User")
                .emailAddress("test@example.com")
                .userRole(UserRole.DONOR)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        when(userService.getUserById(5)).thenReturn(response);

        mockMvc.perform(get("/api/users/5")
                        .with(user("admin@example.com").roles("ADMINISTRATOR"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).getUserById(5);
    }
}
