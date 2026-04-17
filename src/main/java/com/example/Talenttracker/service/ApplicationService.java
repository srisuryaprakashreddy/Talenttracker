package com.example.Talenttracker.service;

import com.example.Talenttracker.dto.ApplicationRequest;
import com.example.Talenttracker.dto.ApplicationResponse;
import com.example.Talenttracker.exception.DuplicateResourceException;
import com.example.Talenttracker.exception.ResourceNotFoundException;
import com.example.Talenttracker.model.Application;
import com.example.Talenttracker.model.Candidate;
import com.example.Talenttracker.model.Job;
import com.example.Talenttracker.model.enums.ApplicationStatus;
import com.example.Talenttracker.model.enums.JobStatus;
import com.example.Talenttracker.repository.ApplicationRepository;
import com.example.Talenttracker.repository.CandidateRepository;
import com.example.Talenttracker.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;

    public ApplicationResponse create(ApplicationRequest request) {
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", request.getJobId()));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new IllegalStateException("Cannot apply — job is not open");
        }

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", request.getCandidateId()));

        if (applicationRepository.existsByJobIdAndCandidateId(job.getId(), candidate.getId())) {
            throw new DuplicateResourceException("Candidate has already applied to this job");
        }

        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();

        return ApplicationResponse.fromEntity(applicationRepository.save(application));
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getById(Long id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
        return ApplicationResponse.fromEntity(app);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId).stream()
                .map(ApplicationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId).stream()
                .map(ApplicationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAll() {
        return applicationRepository.findAll().stream()
                .map(ApplicationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getRecent() {
        return applicationRepository.findTop5ByOrderByAppliedAtDesc().stream()
                .map(ApplicationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countByStatus(ApplicationStatus status) {
        return applicationRepository.countByStatus(status);
    }

    public ApplicationResponse updateStatus(Long id, ApplicationStatus status) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        app.setStatus(status);
        Application saved = applicationRepository.save(app);

        // When a candidate is HIRED, auto-close the job position
        if (status == ApplicationStatus.HIRED) {
            Job job = app.getJob();
            job.setStatus(JobStatus.CLOSED);
            jobRepository.save(job);
        }

        return ApplicationResponse.fromEntity(saved);
    }
}
