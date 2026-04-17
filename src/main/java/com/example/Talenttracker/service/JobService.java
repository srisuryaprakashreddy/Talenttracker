package com.example.Talenttracker.service;

import com.example.Talenttracker.dto.JobRequest;
import com.example.Talenttracker.dto.JobResponse;
import com.example.Talenttracker.dto.PagedResponse;
import com.example.Talenttracker.exception.ResourceNotFoundException;
import com.example.Talenttracker.model.Job;
import com.example.Talenttracker.model.User;
import com.example.Talenttracker.model.enums.JobStatus;
import com.example.Talenttracker.repository.JobRepository;
import com.example.Talenttracker.repository.JobSpecification;
import com.example.Talenttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobResponse create(JobRequest request, Long recruiterId) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", recruiterId));

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .department(request.getDepartment())
                .status(JobStatus.OPEN)
                .postedBy(recruiter)
                .build();

        return JobResponse.fromEntity(jobRepository.save(job));
    }

    @Transactional(readOnly = true)
    public JobResponse getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
        return JobResponse.fromEntity(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAll() {
        return jobRepository.findAll().stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    /**
     * Paginated + filtered job listing.
     * All filter params are optional — pass null to skip a filter.
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobResponse> getAllPaged(String keyword, JobStatus status,
                                                  String department, String location,
                                                  Long recruiterId, Pageable pageable) {
        Specification<Job> spec = Specification
                .where(JobSpecification.hasTitle(keyword))
                .and(JobSpecification.hasStatus(status))
                .and(JobSpecification.hasDepartment(department))
                .and(JobSpecification.hasLocation(location))
                .and(JobSpecification.postedBy(recruiterId));

        Page<Job> page = jobRepository.findAll(spec, pageable);
        return PagedResponse.from(page, JobResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getByRecruiter(Long recruiterId) {
        return jobRepository.findByPostedById(recruiterId).stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getByStatus(JobStatus status) {
        return jobRepository.findByStatus(status).stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> search(String keyword) {
        return jobRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    public JobResponse updateStatus(Long jobId, JobStatus status) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        job.setStatus(status);
        return JobResponse.fromEntity(jobRepository.save(job));
    }
}
