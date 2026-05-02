package org.charityaid.charity_aid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AidRequestCommentRequest {

    @NotBlank(message = "Comment text is required")
    @Size(max = 2000)
    private String commentText;
}
