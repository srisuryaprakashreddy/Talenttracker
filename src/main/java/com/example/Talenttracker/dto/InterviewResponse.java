package com.example.Talenttracker.dto;

import com.example.Talenttracker.model.Interview;
import com.example.Talenttracker.model.enums.InterviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class InterviewResponse {

    private Long id;
    private Long applicationId;
    private Long candidateId;
    private String candidateName;
    private Long jobId;
    private String jobTitle;
    private String interviewerName;
    private LocalDateTime scheduledAt;
    private Integer round;
    private InterviewStatus status;
    private String notes;
    private LocalDateTime createdAt;

    /** Formatted date for Thymeleaf templates */
    public String getDate() {
        return scheduledAt != null
                ? scheduledAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                : "";
    }

    /** Formatted time for Thymeleaf templates */
    public String getTime() {
        return scheduledAt != null
                ? scheduledAt.format(DateTimeFormatter.ofPattern("h:mm a"))
                : "";
    }

    public static InterviewResponse fromEntity(Interview interview) {
        return InterviewResponse.builder()
                .id(interview.getId())
                .applicationId(interview.getApplication().getId())
                .candidateId(interview.getApplication().getCandidate().getId())
                .candidateName(interview.getApplication().getCandidate().getFullName())
                .jobId(interview.getApplication().getJob().getId())
                .jobTitle(interview.getApplication().getJob().getTitle())
                .interviewerName(interview.getInterviewer().getFullName())
                .scheduledAt(interview.getScheduledAt())
                .round(interview.getRound())
                .status(interview.getStatus())
                .notes(interview.getNotes())
                .createdAt(interview.getCreatedAt())
                .build();
    }
}
