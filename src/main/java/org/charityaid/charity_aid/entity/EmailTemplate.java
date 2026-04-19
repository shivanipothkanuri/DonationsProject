package org.charityaid.charity_aid.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_email_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEMPLATE_ID")
    private Integer templateId;

    @Column(name = "TEMPLATE_KEY", nullable = false, unique = true, length = 80)
    private String templateKey;

    @Column(name = "SUBJECT_TEMPLATE", nullable = false, length = 255)
    private String subjectTemplate;

    @Column(name = "BODY_TEMPLATE", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    @Column(name = "UPDATED_BY", length = 150)
    private String updatedBy;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void touch() {
        updatedAt = LocalDateTime.now();
    }
}