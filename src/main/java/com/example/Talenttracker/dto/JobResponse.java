package com.example.Talenttracker.dto;

import com.example.Talenttracker.model.Job;
import com.example.Talenttracker.model.enums.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private String department;
    private JobStatus status;
    private String postedByName;
    private LocalDateTime createdAt;
    private int applicationCount;

    /** Formatted date for templates */
    public String getFormattedDate() {
        return createdAt != null
                ? createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                : "";
    }

    public static JobResponse fromEntity(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .department(job.getDepartment())
                .status(job.getStatus())
                .postedByName(job.getPostedBy().getFullName())
                .createdAt(job.getCreatedAt())
                .applicationCount(job.getApplications() != null
                        ? job.getApplications().size() : 0)
                .build();
    }
}
