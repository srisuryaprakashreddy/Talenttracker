package com.example.Talenttracker.dto;

import com.example.Talenttracker.model.Application;
import com.example.Talenttracker.model.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime createdAt;

    /** Formatted date for templates */
    public String getAppliedDate() {
        return appliedAt != null
                ? appliedAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                : "";
    }

    public static ApplicationResponse fromEntity(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .candidateId(app.getCandidate().getId())
                .candidateName(app.getCandidate().getFullName())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .createdAt(app.getCreatedAt())
                .build();
    }
}
