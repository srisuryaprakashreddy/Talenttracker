package com.example.Talenttracker.controller;

import com.example.Talenttracker.dto.JobRequest;
import com.example.Talenttracker.dto.JobResponse;
import com.example.Talenttracker.dto.PagedResponse;
import com.example.Talenttracker.model.enums.JobStatus;
import com.example.Talenttracker.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job posting and management")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @Operation(summary = "Create a new job", description = "Creates a job posting. Requires ADMIN or RECRUITER role.")
    public ResponseEntity<JobResponse> create(
            @Valid @RequestBody JobRequest request,
            @Parameter(description = "ID of the recruiter posting this job") @RequestParam Long recruiterId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(request, recruiterId));
    }

    @GetMapping
    @Operation(summary = "List jobs (paginated + filtered)",
               description = "Returns a paginated list of jobs. All filter parameters are optional and can be combined.")
    public ResponseEntity<PagedResponse<JobResponse>> getAll(
            @Parameter(description = "Search in job title") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by status: OPEN, CLOSED, ON_HOLD") @RequestParam(required = false) JobStatus status,
            @Parameter(description = "Filter by department") @RequestParam(required = false) String department,
            @Parameter(description = "Filter by location") @RequestParam(required = false) String location,
            @Parameter(description = "Filter by recruiter ID") @RequestParam(required = false) Long recruiterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(jobService.getAllPaged(keyword, status, department, location, recruiterId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID")
    public ResponseEntity<JobResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update job status", description = "Change job status to OPEN, CLOSED, or ON_HOLD. Requires ADMIN or RECRUITER role.")
    public ResponseEntity<JobResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam JobStatus status) {
        return ResponseEntity.ok(jobService.updateStatus(id, status));
    }
}
