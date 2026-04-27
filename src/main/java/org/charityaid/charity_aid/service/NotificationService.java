package org.charityaid.charity_aid.service;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralised email notification service.
 * All methods are @Async so they never block the calling HTTP thread.
 * If mail is not configured (e.g. dev), exceptions are logged and swallowed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationTemplateService notificationTemplateService;
    private final RestClient restClient = RestClient.create();

    @Value("${spring.mail.from:noreply@charityaid.org}")
    private String fromAddress;

    @Value("${brevo.api.key:${BREVO_API_KEY:}}")
    private String brevoApiKey;

    // ── FR-29: Donor email confirmation after donation ────────────────────────

    @Async
    public void sendDonationConfirmation(String toEmail, String donorName,
                                          String campaignTitle, String transactionId,
                                          String donationType, String amount) {
        sendTemplated("DONATION_CONFIRMATION", toEmail, Map.of(
            "donorName", safe(donorName),
            "campaignTitle", safe(campaignTitle),
            "transactionId", safe(transactionId),
            "donationType", safe(donationType),
            "amount", amount != null ? "$" + amount : "N/A"));
    }

    @Async
    public void sendDonationConfirmationWithReceipt(String toEmail, String donorName,
                                          String campaignTitle, String transactionId,
                                          String donationType, String amount,
                                          byte[] receiptPdf, String receiptFileName) {
        Map<String, String> variables = Map.of(
            "donorName", safe(donorName),
            "campaignTitle", safe(campaignTitle),
            "transactionId", safe(transactionId),
            "donationType", safe(donationType),
            "amount", amount != null ? "$" + amount : "N/A"
        );
        String subject = notificationTemplateService.renderSubject("DONATION_CONFIRMATION", variables);
        String body = notificationTemplateService.renderBody("DONATION_CONFIRMATION", variables);
        sendWithAttachment(toEmail, subject, body, receiptPdf, receiptFileName);
    }

    // ── FR-39: Notify staff when campaign reaches 90 % of goal ───────────────

    @Async
    public void sendCampaignNinetyPercentAlert(String toEmail, String staffName,
                                                String campaignTitle, String percentComplete) {
        sendTemplated("CAMPAIGN_90_ALERT", toEmail, Map.of(
            "staffName", safe(staffName),
            "campaignTitle", safe(campaignTitle),
            "percentComplete", safe(percentComplete)));
    }

    // ── FR-60: Notify beneficiary when aid request is approved ───────────────

    @Async
    public void sendAidRequestApproved(String toEmail, String beneficiaryName,
                                        Integer requestId, String aidType) {
        sendTemplated("AID_REQUEST_APPROVED", toEmail, Map.of(
            "beneficiaryName", safe(beneficiaryName),
            "requestId", String.valueOf(requestId),
            "aidType", safe(aidType)));
    }

    // ── FR-61: Notify beneficiary when aid request is denied ─────────────────

    @Async
    public void sendAidRequestDenied(String toEmail, String beneficiaryName,
                                      Integer requestId, String aidType, String justification) {
        sendTemplated("AID_REQUEST_DENIED", toEmail, Map.of(
            "beneficiaryName", safe(beneficiaryName),
            "requestId", String.valueOf(requestId),
            "aidType", safe(aidType),
            "justification", safe(justification != null ? justification : "N/A")));
    }

    // ── FR-55: Escalation alert to administrators ─────────────────────────

    @Async
    public void sendEscalationAlert(String toEmail, String adminName, Integer requestId,
                                    String beneficiaryName, String aidType, String caseManagerName) {
        sendTemplated("ESCALATION_ALERT", toEmail, Map.of(
            "adminName", safe(adminName),
            "requestId", String.valueOf(requestId),
            "beneficiaryName", safe(beneficiaryName),
            "aidType", safe(aidType),
            "caseManagerName", safe(caseManagerName)));
    }

    // ── FR-10: Password reset link ─────────────────────────────────────────

    @Async
    public void sendPasswordResetLink(String toEmail, String fullName, String resetToken,
                                       String appBaseUrl) {
        String resetUrl = appBaseUrl + "/index.html?reset=" + resetToken;
        sendTemplated("PASSWORD_RESET", toEmail, Map.of(
            "fullName", safe(fullName),
            "expiryMinutes", String.valueOf(org.charityaid.charity_aid.entity.PasswordResetToken.EXPIRY_MINUTES),
            "resetUrl", resetUrl));
        }

        @Async
        public void sendFulfillmentNoticeToCaseManager(String toEmail, String caseManagerName,
                               Integer requestId, String beneficiaryName,
                               String aidType, String staffName) {
        sendTemplated("FULFILLMENT_NOTICE", toEmail, Map.of(
            "caseManagerName", safe(caseManagerName),
            "requestId", String.valueOf(requestId),
            "beneficiaryName", safe(beneficiaryName),
            "aidType", safe(aidType),
            "staffName", safe(staffName)));
        }

        @Async
        public void sendPendingApprovalReminder(String toEmail, String caseManagerName,
                            Integer requestId, String beneficiaryName,
                            String submissionDate) {
        sendTemplated("PENDING_APPROVAL_REMINDER", toEmail, Map.of(
            "caseManagerName", safe(caseManagerName),
            "requestId", String.valueOf(requestId),
            "beneficiaryName", safe(beneficiaryName),
            "submissionDate", safe(submissionDate)));
        }

        @Async
        public void sendBulkCampaignUpdate(String toEmail, String donorName, String campaignTitle, String message) {
        sendTemplated("BULK_CAMPAIGN_UPDATE", toEmail, Map.of(
            "donorName", safe(donorName),
            "campaignTitle", safe(campaignTitle),
            "message", safe(message)));
        }

        @Async
        public void sendHighValueDonationAlert(String toEmail, String leaderName, String donorName,
                           String campaignTitle, String amount, String threshold) {
        sendTemplated("HIGH_VALUE_DONATION_ALERT", toEmail, Map.of(
            "leaderName", safe(leaderName),
            "donorName", safe(donorName),
            "campaignTitle", safe(campaignTitle),
            "amount", safe(amount),
            "threshold", safe(threshold)));
    }

    // ── private helper ────────────────────────────────────────────────────────

        private void sendTemplated(String templateKey, String to, Map<String, String> variables) {
        String subject = notificationTemplateService.renderSubject(templateKey, variables);
        String body = notificationTemplateService.renderBody(templateKey, variables);
        send(to, subject, body);
        }

        private String safe(String value) {
        return value != null ? value : "";
        }

    private void send(String to, String subject, String text) {
        if (to == null || to.isBlank()) {
            log.debug("Skipping email – recipient address is blank (subject: {})", subject);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            log.info("Email sent to {} | subject: {}", to, subject);
        } catch (MailException ex) {
            log.error("Failed to send email to {} | subject: {} | error: {}", to, subject, ex.getMessage());
            if (sendViaBrevoApi(to, subject, text, null, null)) {
                log.info("Email sent via Brevo HTTP API to {} | subject: {}", to, subject);
            }
        }
    }

    private void sendWithAttachment(String to, String subject, String text,
                                    byte[] attachmentBytes, String attachmentFileName) {
        if (to == null || to.isBlank()) {
            log.debug("Skipping email – recipient address is blank (subject: {})", subject);
            return;
        }
        if (attachmentBytes == null || attachmentBytes.length == 0) {
            send(to, subject, text);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            String safeFrom = fromAddress != null ? fromAddress : "noreply@charityaid.org";
            String safeSubject = subject != null ? subject : "Notification";
            String safeText = text != null ? text : "";

            helper.setFrom(safeFrom);
            helper.setTo(to);
            helper.setSubject(safeSubject);
            helper.setText(safeText);
            helper.addAttachment(
                    attachmentFileName != null && !attachmentFileName.isBlank()
                            ? attachmentFileName
                            : "donation-receipt.pdf",
                    () -> new java.io.ByteArrayInputStream(attachmentBytes));

            mailSender.send(mimeMessage);
            log.info("Email sent to {} with attachment | subject: {}", to, subject);
        } catch (MailException | MessagingException ex) {
            log.error("Failed to send email with attachment to {} | subject: {} | error: {}", to, subject, ex.getMessage());
            if (sendViaBrevoApi(to, subject, text, attachmentBytes, attachmentFileName)) {
                log.info("Email with attachment sent via Brevo HTTP API to {} | subject: {}", to, subject);
                return;
            }
            send(to, subject, text);
        }
    }

    private boolean sendViaBrevoApi(String to, String subject, String text,
                                    byte[] attachmentBytes, String attachmentFileName) {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            log.warn("BREVO_API_KEY is not configured; cannot use HTTP fallback for {}", to);
            return false;
        }

        try {
            String safeFrom = fromAddress != null && !fromAddress.isBlank()
                    ? fromAddress
                    : "noreply@charityaid.org";
            String safeSubject = subject != null ? subject : "Notification";
            String safeText = text != null ? text : "";

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sender", Map.of("email", safeFrom));
            payload.put("to", List.of(Map.of("email", to)));
            payload.put("subject", safeSubject);
            payload.put("textContent", safeText);

            if (attachmentBytes != null && attachmentBytes.length > 0) {
                payload.put("attachment", List.of(Map.of(
                        "name", (attachmentFileName != null && !attachmentFileName.isBlank())
                                ? attachmentFileName
                                : "attachment.pdf",
                        "content", Base64.getEncoder().encodeToString(attachmentBytes)
                )));
            }

            restClient.post()
                    .uri("https://api.brevo.com/v3/smtp/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("api-key", brevoApiKey)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            return true;
        } catch (RestClientException ex) {
            log.error("Brevo HTTP API fallback failed for {} | subject: {} | error: {}", to, subject, ex.getMessage());
            return false;
        }
    }
}
