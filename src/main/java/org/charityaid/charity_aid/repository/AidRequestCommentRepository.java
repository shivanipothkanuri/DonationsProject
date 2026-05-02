package org.charityaid.charity_aid.repository;

import java.util.List;

import org.charityaid.charity_aid.entity.AidRequestComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AidRequestCommentRepository extends JpaRepository<AidRequestComment, Integer> {

    List<AidRequestComment> findByAidRequest_RequestIdOrderByCreatedAtAsc(Integer requestId);
}
