package org.charityaid.charity_aid.repository;

import java.util.Optional;

import org.charityaid.charity_aid.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Integer> {

    Optional<EmailTemplate> findByTemplateKey(String templateKey);
}