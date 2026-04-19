package org.charityaid.charity_aid.controller;

import java.util.List;

import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.EmailTemplateRequest;
import org.charityaid.charity_aid.dto.EmailTemplateResponse;
import org.charityaid.charity_aid.service.NotificationTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notification-templates")
@RequiredArgsConstructor
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<List<EmailTemplateResponse>>> getTemplates() {
        return ResponseEntity.ok(ApiResponse.ok(notificationTemplateService.getAllTemplates()));
    }

    @GetMapping("/{templateKey}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> getTemplate(@PathVariable String templateKey) {
        return ResponseEntity.ok(ApiResponse.ok(notificationTemplateService.getTemplate(templateKey)));
    }

    @PutMapping("/{templateKey}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> updateTemplate(
            @PathVariable String templateKey,
            @Valid @RequestBody EmailTemplateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Template updated",
                notificationTemplateService.updateTemplate(templateKey, request, userDetails.getUsername())));
    }
}