package com.example.Talenttracker.controller;

import com.example.Talenttracker.dto.CandidateRequest;
import com.example.Talenttracker.dto.CandidateResponse;
import com.example.Talenttracker.dto.PagedResponse;
import com.example.Talenttracker.service.CandidateService;
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
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidates", description = "Candidate profile management")
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping
    @Operation(summary = "Create a candidate", description = "Registers a new candidate profile. Requires ADMIN or RECRUITER role.")
    public ResponseEntity<CandidateResponse> create(@Valid @RequestBody CandidateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.create(request));
    }

    @GetMapping
    @Operation(summary = "List candidates (paginated + filtered)",
               description = "Returns a paginated list of candidates. All filter parameters are optional.")
    public ResponseEntity<PagedResponse<CandidateResponse>> getAll(
            @Parameter(description = "Search by candidate name") @RequestParam(required = false) String name,
            @Parameter(description = "Search by email") @RequestParam(required = false) String email,
            @Parameter(description = "Search by skill keyword") @RequestParam(required = false) String skill,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(candidateService.getAllPaged(name, email, skill, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get candidate by ID")
    public ResponseEntity<CandidateResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update candidate", description = "Updates an existing candidate profile. Requires ADMIN or RECRUITER role.")
    public ResponseEntity<CandidateResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CandidateRequest request) {
        return ResponseEntity.ok(candidateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete candidate", description = "Permanently deletes a candidate profile. Requires ADMIN role.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
