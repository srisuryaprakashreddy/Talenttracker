package com.example.Talenttracker.dto;

import com.example.Talenttracker.model.enums.FeedbackVerdict;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackRequest {

    @NotNull(message = "Interview ID is required")
    private Long interviewId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotNull(message = "Verdict is required")
    private FeedbackVerdict verdict;

    private String comments;
}
