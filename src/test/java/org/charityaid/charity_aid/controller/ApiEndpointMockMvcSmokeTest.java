package org.charityaid.charity_aid.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.List;

import org.charityaid.charity_aid.service.AidRequestService;
import org.charityaid.charity_aid.service.AuditService;
import org.charityaid.charity_aid.service.AuthService;
import org.charityaid.charity_aid.service.BeneficiaryService;
import org.charityaid.charity_aid.service.CampaignService;
import org.charityaid.charity_aid.service.DonationService;
import org.charityaid.charity_aid.service.InventoryService;
import org.charityaid.charity_aid.service.NotificationTemplateService;
import org.charityaid.charity_aid.service.ReportService;
import org.charityaid.charity_aid.service.UserService;
import org.charityaid.charity_aid.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = {
        AidRequestController.class,
        AuditController.class,
        AuthController.class,
        BeneficiaryController.class,
        CampaignController.class,
        DonationController.class,
        InventoryController.class,
        NotificationTemplateController.class,
        ReportController.class,
        UserController.class
})
@AutoConfigureMockMvc
class ApiEndpointMockMvcSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AidRequestService aidRequestService;

    @MockBean
    private AuditService auditService;

    @MockBean
    private AuthService authService;

    @MockBean
    private BeneficiaryService beneficiaryService;

    @MockBean
    private CampaignService campaignService;

    @MockBean
    private DonationService donationService;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private NotificationTemplateService notificationTemplateService;

    @MockBean
    private ReportService reportService;

    @MockBean
    private UserService userService;

        @MockBean
        private JwtAuthFilter jwtAuthFilter;

    @Test
    void allMappedEndpoints_shouldNotReturnServerError() throws Exception {
        List<EndpointSpec> endpoints = List.of(
                new EndpointSpec("GET", "/api/aid-requests", null),
                new EndpointSpec("GET", "/api/aid-requests/1", null),
                new EndpointSpec("GET", "/api/aid-requests/beneficiary/1", null),
                new EndpointSpec("POST", "/api/aid-requests", "{}"),
                new EndpointSpec("PUT", "/api/aid-requests/1", "{}"),
                new EndpointSpec("PATCH", "/api/aid-requests/1/approve", "{}"),
                new EndpointSpec("PATCH", "/api/aid-requests/1/deny?justification=test", "{}"),
                new EndpointSpec("PATCH", "/api/aid-requests/1/fulfill", "{}"),
                new EndpointSpec("GET", "/api/aid-requests/1/history", null),
                new EndpointSpec("PATCH", "/api/aid-requests/1/escalate", "{}"),

                new EndpointSpec("GET", "/api/audit", null),

                new EndpointSpec("POST", "/api/auth/register", "{}"),
                new EndpointSpec("POST", "/api/auth/login", "{}"),
                new EndpointSpec("GET", "/api/auth/setup-needed", null),
                new EndpointSpec("POST", "/api/auth/setup", "{}"),
                new EndpointSpec("POST", "/api/auth/forgot-password", "{}"),
                new EndpointSpec("POST", "/api/auth/reset-password", "{}"),
                new EndpointSpec("POST", "/api/auth/logout", "{}"),
                new EndpointSpec("POST", "/api/auth/mfa/setup", "{}"),
                new EndpointSpec("POST", "/api/auth/mfa/confirm", "{}"),
                new EndpointSpec("POST", "/api/auth/mfa/verify", "{}"),
                new EndpointSpec("DELETE", "/api/auth/mfa", null),

                new EndpointSpec("GET", "/api/beneficiaries", null),
                new EndpointSpec("GET", "/api/beneficiaries/1", null),
                new EndpointSpec("POST", "/api/beneficiaries", "{}"),
                new EndpointSpec("PUT", "/api/beneficiaries/1", "{}"),
                new EndpointSpec("PATCH", "/api/beneficiaries/1/deactivate", "{}"),

                new EndpointSpec("GET", "/api/campaigns/active", null),
                new EndpointSpec("GET", "/api/campaigns", null),
                new EndpointSpec("GET", "/api/campaigns/1", null),
                new EndpointSpec("POST", "/api/campaigns", "{}"),
                new EndpointSpec("PUT", "/api/campaigns/1", "{}"),
                new EndpointSpec("PATCH", "/api/campaigns/1/close", "{}"),
                new EndpointSpec("DELETE", "/api/campaigns/1", null),
                new EndpointSpec("POST", "/api/campaigns/1/bulk-update", "{}"),

                new EndpointSpec("POST", "/api/donations", "{}"),
                new EndpointSpec("GET", "/api/donations/in-kind-options", null),
                new EndpointSpec("POST", "/api/donations/recurring", "{}"),
                new EndpointSpec("GET", "/api/donations/recurring/my", null),
                new EndpointSpec("DELETE", "/api/donations/recurring/1", null),
                new EndpointSpec("GET", "/api/donations/my", null),
                new EndpointSpec("GET", "/api/donations/1", null),
                new EndpointSpec("GET", "/api/donations/1/receipt", null),

                new EndpointSpec("GET", "/api/inventory", null),
                new EndpointSpec("GET", "/api/inventory/low-stock", null),
                new EndpointSpec("GET", "/api/inventory/1", null),
                new EndpointSpec("POST", "/api/inventory", "{}"),
                new EndpointSpec("PUT", "/api/inventory/1", "{}"),
                new EndpointSpec("DELETE", "/api/inventory/1", null),

                new EndpointSpec("GET", "/api/notification-templates", null),
                new EndpointSpec("GET", "/api/notification-templates/donation_confirmation", null),
                new EndpointSpec("PUT", "/api/notification-templates/donation_confirmation", "{}"),

                new EndpointSpec("GET", "/api/reports/donations", null),
                new EndpointSpec("GET", "/api/reports/campaigns", null),
                new EndpointSpec("GET", "/api/reports/aid-requests/pending", null),
                new EndpointSpec("GET", "/api/reports/aid-requests/fulfilled", null),
                new EndpointSpec("GET", "/api/reports/kpi", null),
                new EndpointSpec("GET", "/api/reports/donations/csv", null),
                new EndpointSpec("GET", "/api/reports/compliance", null),

                new EndpointSpec("GET", "/api/users/me", null),
                new EndpointSpec("PUT", "/api/users/me", "{}"),
                new EndpointSpec("PUT", "/api/users/me/password", "{}"),
                new EndpointSpec("GET", "/api/users", null),
                new EndpointSpec("GET", "/api/users/1", null),
                new EndpointSpec("POST", "/api/users", "{}"),
                new EndpointSpec("PATCH", "/api/users/1/role?role=STAFF", "{}"),
                new EndpointSpec("PATCH", "/api/users/1/deactivate", "{}"),
                new EndpointSpec("PATCH", "/api/users/1/reactivate", "{}"),
                new EndpointSpec("PATCH", "/api/users/1/suspend", "{}"),
                new EndpointSpec("PATCH", "/api/users/1/unlock", "{}")
        );

        for (EndpointSpec endpoint : endpoints) {
            ResultActions result = perform(endpoint);
            int status = result.andReturn().getResponse().getStatus();
            assertTrue(status < 500, () -> "Expected non-5xx for " + endpoint.method + " " + endpoint.path + ", got " + status);
        }
    }

    private ResultActions perform(EndpointSpec endpoint) throws Exception {
        return switch (endpoint.method) {
            case "GET" -> mockMvc.perform(get(endpoint.path).with(user("test@example.com").roles("ADMINISTRATOR", "STAFF", "DONOR", "CASE_MANAGER", "LEADERSHIP")));
            case "POST" -> mockMvc.perform(post(endpoint.path)
                    .with(user("test@example.com").roles("ADMINISTRATOR", "STAFF", "DONOR", "CASE_MANAGER", "LEADERSHIP"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(endpoint.body == null ? "{}" : endpoint.body));
            case "PUT" -> mockMvc.perform(put(endpoint.path)
                    .with(user("test@example.com").roles("ADMINISTRATOR", "STAFF", "DONOR", "CASE_MANAGER", "LEADERSHIP"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(endpoint.body == null ? "{}" : endpoint.body));
            case "PATCH" -> mockMvc.perform(patch(endpoint.path)
                    .with(user("test@example.com").roles("ADMINISTRATOR", "STAFF", "DONOR", "CASE_MANAGER", "LEADERSHIP"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(endpoint.body == null ? "{}" : endpoint.body));
            case "DELETE" -> mockMvc.perform(delete(endpoint.path)
                    .with(user("test@example.com").roles("ADMINISTRATOR", "STAFF", "DONOR", "CASE_MANAGER", "LEADERSHIP")));
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + endpoint.method);
        };
    }

    private record EndpointSpec(String method, String path, String body) {
    }
}
