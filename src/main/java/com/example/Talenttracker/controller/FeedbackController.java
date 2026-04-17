package com.example.Talenttracker.controller;

import com.example.Talenttracker.dto.FeedbackRequest;
import com.example.Talenttracker.dto.FeedbackResponse;
import com.example.Talenttracker.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Post-interview feedback and verdicts")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "Submit feedback", description = "Submit interview feedback with a 1-5 rating and verdict. Interview must be COMPLETED. Requires ADMIN or INTERVIEWER role.")
    public ResponseEntity<FeedbackResponse> create(
            @Valid @RequestBody FeedbackRequest request,
            @Parameter(description = "ID of the user giving feedback") @RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.create(request, userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get feedback by ID")
    public ResponseEntity<FeedbackResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getById(id));
    }

    @GetMapping("/interview/{interviewId}")
    @Operation(summary = "List feedback for an interview", description = "Returns all feedback submitted for a specific interview")
    public ResponseEntity<List<FeedbackResponse>> getByInterview(@PathVariable Long interviewId) {
        return ResponseEntity.ok(feedbackService.getByInterview(interviewId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List feedback by user", description = "Returns all feedback submitted by a specific interviewer")
    public ResponseEntity<List<FeedbackResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(feedbackService.getByUser(userId));
    }
}
