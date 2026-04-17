package com.example.Talenttracker.dto;

import com.example.Talenttracker.model.User;
import com.example.Talenttracker.model.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private int jobCount;
    private int interviewCount;
    private LocalDateTime createdAt;
    
    public String getFormattedDate() {
        return createdAt != null ? createdAt.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")) : "";
    }

    public static UserResponse fromEntity(User user) {
        int jobs = 0;
        int interviews = 0;
        try { jobs = user.getPostedJobs() != null ? user.getPostedJobs().size() : 0; } catch (Exception ignored) {}
        try { interviews = user.getInterviews() != null ? user.getInterviews().size() : 0; } catch (Exception ignored) {}

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .jobCount(jobs)
                .interviewCount(interviews)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
