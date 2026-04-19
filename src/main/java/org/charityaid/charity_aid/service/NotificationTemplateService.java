package org.charityaid.charity_aid.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.charityaid.charity_aid.dto.EmailTemplateRequest;
import org.charityaid.charity_aid.dto.EmailTemplateResponse;
import org.charityaid.charity_aid.entity.EmailTemplate;
import org.charityaid.charity_aid.repository.EmailTemplateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    private static final Map<String, TemplateDefinition> DEFAULT_TEMPLATES = createDefaultTemplates();

    public List<EmailTemplateResponse> getAllTemplates() {
        return DEFAULT_TEMPLATES.keySet().stream()
                .map(this::getTemplate)
                .toList();
    }

    public EmailTemplateResponse getTemplate(String templateKey) {
        String key = normalize(templateKey);
        TemplateDefinition defaults = getDefaults(key);

        return emailTemplateRepository.findByTemplateKey(key)
                .map(EmailTemplateResponse::from)
                .orElseGet(() -> EmailTemplateResponse.builder()
                        .templateKey(key)
                        .subjectTemplate(defaults.subject())
                        .bodyTemplate(defaults.body())
                        .updatedBy("SYSTEM_DEFAULT")
                        .updatedAt((LocalDateTime) null)
                        .build());
    }

    @Transactional
    public EmailTemplateResponse updateTemplate(String templateKey, EmailTemplateRequest request, String updatedBy) {
        String key = normalize(templateKey);
        getDefaults(key);

        EmailTemplate template = emailTemplateRepository.findByTemplateKey(key)
                .orElseGet(() -> EmailTemplate.builder().templateKey(key).build());

        template.setSubjectTemplate(request.getSubjectTemplate());
        template.setBodyTemplate(request.getBodyTemplate());
        template.setUpdatedBy(updatedBy);

        return EmailTemplateResponse.from(emailTemplateRepository.save(template));
    }

    public String renderSubject(String templateKey, Map<String, String> variables) {
        String key = normalize(templateKey);
        TemplateDefinition defaults = getDefaults(key);
        String subject = emailTemplateRepository.findByTemplateKey(key)
                .map(EmailTemplate::getSubjectTemplate)
                .orElse(defaults.subject());
        return render(subject, variables);
    }

    public String renderBody(String templateKey, Map<String, String> variables) {
        String key = normalize(templateKey);
        TemplateDefinition defaults = getDefaults(key);
        String body = emailTemplateRepository.findByTemplateKey(key)
                .map(EmailTemplate::getBodyTemplate)
                .orElse(defaults.body());
        return render(body, variables);
    }

    private String render(String template, Map<String, String> variables) {
        String rendered = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue() != null ? entry.getValue() : "");
        }
        return rendered;
    }

    private String normalize(String templateKey) {
        return templateKey == null ? null : templateKey.trim().toUpperCase();
    }

    private TemplateDefinition getDefaults(String templateKey) {
        TemplateDefinition templateDefinition = DEFAULT_TEMPLATES.get(templateKey);
        if (templateDefinition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown template key: " + templateKey);
        }
        return templateDefinition;
    }

    private static Map<String, TemplateDefinition> createDefaultTemplates() {
        Map<String, TemplateDefinition> defaults = new LinkedHashMap<>();
        defaults.put("DONATION_CONFIRMATION", new TemplateDefinition(
                "Thank you for your donation - {{campaignTitle}}",
                "Dear {{donorName}},\n\nYour {{donationType}} donation has been successfully recorded.\n\nCampaign   : {{campaignTitle}}\nTransaction: {{transactionId}}\nAmount     : {{amount}}\n\nThank you for your generosity!\n\n- CharityAid Team"));
        defaults.put("CAMPAIGN_90_ALERT", new TemplateDefinition(
                "Campaign Alert: {{campaignTitle}} has reached {{percentComplete}}% of its goal",
                "Dear {{staffName}},\n\nThe campaign \"{{campaignTitle}}\" has reached {{percentComplete}}% of its fundraising goal.\n\nPlease review the campaign and prepare for completion.\n\n- CharityAid System"));
        defaults.put("AID_REQUEST_APPROVED", new TemplateDefinition(
                "Your aid request has been approved",
                "Dear {{beneficiaryName}},\n\nYour aid request (ID: {{requestId}}) for {{aidType}} has been APPROVED.\n\nOur staff will be in touch with you shortly to arrange fulfilment.\n\n- CharityAid Team"));
        defaults.put("AID_REQUEST_DENIED", new TemplateDefinition(
                "Update on your aid request",
                "Dear {{beneficiaryName}},\n\nWe regret to inform you that your aid request (ID: {{requestId}}) for {{aidType}} has been DENIED.\n\nReason: {{justification}}\n\nIf you have questions, please contact our office.\n\n- CharityAid Team"));
        defaults.put("ESCALATION_ALERT", new TemplateDefinition(
                "URGENT: Aid Request #{{requestId}} Escalated for Review",
                "Dear {{adminName}},\n\nCase Manager {{caseManagerName}} has escalated the following aid request for your attention:\n\nRequest ID   : {{requestId}}\nBeneficiary  : {{beneficiaryName}}\nAid Type     : {{aidType}}\n\nPlease log in to the system to review and take action.\n\n- CharityAid System"));
        defaults.put("PASSWORD_RESET", new TemplateDefinition(
                "Password Reset Request - CharityAid",
                "Dear {{fullName}},\n\nWe received a request to reset your CharityAid account password.\n\nClick the link below (or paste it into your browser) to set a new password.\nThis link expires in {{expiryMinutes}} minutes.\n\n{{resetUrl}}\n\nIf you did not request a password reset, please ignore this email.\n\n- CharityAid Team"));
        defaults.put("FULFILLMENT_NOTICE", new TemplateDefinition(
                "Aid Request #{{requestId}} fulfilled",
                "Dear {{caseManagerName}},\n\nAid request #{{requestId}} for {{beneficiaryName}} ({{aidType}}) has been fulfilled by {{staffName}}.\n\n- CharityAid System"));
        defaults.put("PENDING_APPROVAL_REMINDER", new TemplateDefinition(
                "Reminder: Aid Request #{{requestId}} pending approval",
                "Dear {{caseManagerName}},\n\nAid request #{{requestId}} for {{beneficiaryName}} has been pending approval since {{submissionDate}}.\n\nPlease review it as soon as possible.\n\n- CharityAid System"));
        defaults.put("BULK_CAMPAIGN_UPDATE", new TemplateDefinition(
                "Update on campaign {{campaignTitle}}",
                "Dear {{donorName}},\n\nHere is an update about campaign {{campaignTitle}}:\n\n{{message}}\n\nThank you for supporting CharityAid.\n\n- CharityAid Team"));
        defaults.put("HIGH_VALUE_DONATION_ALERT", new TemplateDefinition(
                "High-value donation received for {{campaignTitle}}",
                "Dear {{leaderName}},\n\nA donation of {{amount}} has been received from {{donorName}} for campaign {{campaignTitle}}.\nThis exceeded the configured high-value threshold of {{threshold}}.\n\n- CharityAid System"));
        return defaults;
    }

    private record TemplateDefinition(String subject, String body) {
    }
}