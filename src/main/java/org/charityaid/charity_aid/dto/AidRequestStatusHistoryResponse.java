package org.charityaid.charity_aid.dto;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.AidRequestStatusHistory;
import org.charityaid.charity_aid.entity.RequestStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AidRequestStatusHistoryResponse {

    private Integer historyId;
    private RequestStatus status;
    private LocalDateTime changedAt;
    private String actorName;
    private String notes;

    public static AidRequestStatusHistoryResponse from(AidRequestStatusHistory h) {
        return AidRequestStatusHistoryResponse.builder()
                .historyId(h.getHistoryId())
                .status(h.getStatus())
                .changedAt(h.getChangedAt())
                .actorName(h.getActor() != null ? h.getActor().getFullName() : null)
                .notes(h.getNotes())
                .build();
    }
}
