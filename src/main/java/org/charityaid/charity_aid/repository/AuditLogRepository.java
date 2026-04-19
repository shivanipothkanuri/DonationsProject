package org.charityaid.charity_aid.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.charityaid.charity_aid.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);

    Page<AuditLog> findByPerformedBy(String performedBy, Pageable pageable);

    @Query("SELECT a FROM AuditLog a " +
           "WHERE (:from IS NULL OR a.performedAt >= :from) " +
           "AND (:to IS NULL OR a.performedAt <= :to) " +
           "AND (:entityType IS NULL OR a.entityType = :entityType) " +
           "AND (:performedBy IS NULL OR a.performedBy = :performedBy) " +
           "ORDER BY a.performedAt DESC")
    List<AuditLog> findForComplianceReport(@Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to,
                                           @Param("entityType") String entityType,
                                           @Param("performedBy") String performedBy);
}
