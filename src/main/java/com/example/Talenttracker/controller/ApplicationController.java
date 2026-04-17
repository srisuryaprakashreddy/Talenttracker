package com.example.Talenttracker.controller;

import com.example.Talenttracker.dto.ApplicationRequest;
import com.example.Talenttracker.dto.ApplicationResponse;
import com.example.Talenttracker.model.enums.ApplicationStatus;
import com.example.Talenttracker.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Job application tracking and status management")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Submit an application", description = "Creates a new application linking a candidate to a job. Fails if the job is not OPEN or the candidate already applied.")
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    @Operation(summary = "List applications by candidate", description = "Returns all applications submitted by a specific candidate")
    public ResponseEntity<List<ApplicationResponse>> getByCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(applicationService.getByCandidate(candidateId));
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "List applications for a job", description = "Returns all applications received for a specific job posting")
    public ResponseEntity<List<ApplicationResponse>> getByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getByJob(jobId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update application status", description = "Advance the application through the pipeline: APPLIED → SHORTLISTED → INTERVIEWING → OFFERED/REJECTED/HIRED")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }
}
