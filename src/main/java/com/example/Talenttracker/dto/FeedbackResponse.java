package com.example.Talenttracker.dto;

import com.example.Talenttracker.model.Feedback;
import com.example.Talenttracker.model.enums.FeedbackVerdict;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class FeedbackResponse {

    private Long id;
    private Long interviewId;
    private Long candidateId;
    private String candidateName;
    private Long jobId;
    private String jobTitle;
    private String interviewerName;
    private String givenByName;
    private Integer rating;
    private FeedbackVerdict verdict;
    private String comments;
    private LocalDateTime createdAt;

    /** Human-readable verdict label for templates */
    public String getVerdictLabel() {
        if (verdict == null) return "";
        return switch (verdict) {
            case STRONG_YES -> "Strong Yes";
            case YES -> "Yes";
            case NEUTRAL -> "Neutral";
            case NO -> "No";
            case STRONG_NO -> "Strong No";
        };
    }

    /** Formatted date */
    public String getDate() {
        return createdAt != null
                ? createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                : "";
    }

    public static FeedbackResponse fromEntity(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .interviewId(feedback.getInterview().getId())
                .candidateId(feedback.getInterview().getApplication().getCandidate().getId())
                .candidateName(feedback.getInterview().getApplication().getCandidate().getFullName())
                .jobId(feedback.getInterview().getApplication().getJob().getId())
                .jobTitle(feedback.getInterview().getApplication().getJob().getTitle())
                .interviewerName(feedback.getInterview().getInterviewer().getFullName())
                .givenByName(feedback.getGivenBy().getFullName())
                .rating(feedback.getRating())
                .verdict(feedback.getVerdict())
                .comments(feedback.getComments())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
