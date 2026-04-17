package com.example.Talenttracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    private String description;

    private String location;

    private String department;
}
