package org.charityaid.charity_aid.dto;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.AidRequestComment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AidRequestCommentResponse {

    private Integer commentId;
    private Integer requestId;
    private Integer authorId;
    private String authorName;
    private String commentText;
    private LocalDateTime createdAt;

    public static AidRequestCommentResponse from(AidRequestComment c) {
        return AidRequestCommentResponse.builder()
                .commentId(c.getCommentId())
                .requestId(c.getAidRequest().getRequestId())
                .authorId(c.getAuthor().getUserId())
                .authorName(c.getAuthor().getFullName())
                .commentText(c.getCommentText())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
