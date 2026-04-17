package com.example.Talenttracker.dto;

import com.example.Talenttracker.model.Candidate;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class CandidateResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String resumeUrl;
    private String skills;
    private LocalDateTime createdAt;
    private int applicationCount;

    /** Returns skills as a list for th:each rendering */
    public List<String> getSkillList() {
        if (skills == null || skills.isBlank()) return Collections.emptyList();
        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public static CandidateResponse fromEntity(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .fullName(candidate.getFullName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .resumeUrl(candidate.getResumeUrl())
                .skills(candidate.getSkills())
                .createdAt(candidate.getCreatedAt())
                .applicationCount(candidate.getApplications() != null
                        ? candidate.getApplications().size() : 0)
                .build();
    }
}
