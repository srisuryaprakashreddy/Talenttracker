package com.example.Talenttracker.controller;

import com.example.Talenttracker.dto.InterviewRequest;
import com.example.Talenttracker.dto.InterviewResponse;
import com.example.Talenttracker.model.enums.InterviewStatus;
import com.example.Talenttracker.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Tag(name = "Interviews", description = "Interview scheduling and status tracking")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    @Operation(summary = "Schedule an interview", description = "Schedules a new interview for an application. Requires ADMIN or RECRUITER role.")
    public ResponseEntity<InterviewResponse> schedule(@Valid @RequestBody InterviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewService.schedule(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get interview by ID")
    public ResponseEntity<InterviewResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.getById(id));
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "List interviews for an application", description = "Returns all interview rounds for a specific application")
    public ResponseEntity<List<InterviewResponse>> getByApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(interviewService.getByApplication(applicationId));
    }

    @GetMapping("/interviewer/{interviewerId}")
    @Operation(summary = "List interviews by interviewer", description = "Returns all interviews assigned to a specific interviewer")
    public ResponseEntity<List<InterviewResponse>> getByInterviewer(@PathVariable Long interviewerId) {
        return ResponseEntity.ok(interviewService.getByInterviewer(interviewerId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update interview status", description = "Mark interview as COMPLETED or CANCELLED")
    public ResponseEntity<InterviewResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam InterviewStatus status) {
        return ResponseEntity.ok(interviewService.updateStatus(id, status));
    }
}
