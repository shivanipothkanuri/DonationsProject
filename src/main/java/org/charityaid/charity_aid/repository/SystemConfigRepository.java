package org.charityaid.charity_aid.repository;

import org.charityaid.charity_aid.entity.SystemConfigEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository extends JpaRepository<SystemConfigEntry, String> {
}
