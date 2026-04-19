package org.charityaid.charity_aid.dto;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.EmailTemplate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailTemplateResponse {

    private String templateKey;
    private String subjectTemplate;
    private String bodyTemplate;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public static EmailTemplateResponse from(EmailTemplate template) {
        return EmailTemplateResponse.builder()
                .templateKey(template.getTemplateKey())
                .subjectTemplate(template.getSubjectTemplate())
                .bodyTemplate(template.getBodyTemplate())
                .updatedBy(template.getUpdatedBy())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}