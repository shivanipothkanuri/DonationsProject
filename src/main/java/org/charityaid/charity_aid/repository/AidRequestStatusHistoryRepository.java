package org.charityaid.charity_aid.repository;

import java.util.List;

import org.charityaid.charity_aid.entity.AidRequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AidRequestStatusHistoryRepository extends JpaRepository<AidRequestStatusHistory, Integer> {

    List<AidRequestStatusHistory> findByAidRequest_RequestIdOrderByChangedAtAsc(Integer requestId);
}
