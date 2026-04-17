package com.example.Talenttracker.service;

import com.example.Talenttracker.dto.CandidateRequest;
import com.example.Talenttracker.dto.CandidateResponse;
import com.example.Talenttracker.dto.PagedResponse;
import com.example.Talenttracker.exception.DuplicateResourceException;
import com.example.Talenttracker.exception.ResourceNotFoundException;
import com.example.Talenttracker.model.Candidate;
import com.example.Talenttracker.repository.CandidateRepository;
import com.example.Talenttracker.repository.CandidateSpecification;
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
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateResponse create(CandidateRequest request) {
        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Candidate already exists with email: " + request.getEmail());
        }

        Candidate candidate = Candidate.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .resumeUrl(request.getResumeUrl())
                .skills(request.getSkills())
                .build();

        return CandidateResponse.fromEntity(candidateRepository.save(candidate));
    }

    @Transactional(readOnly = true)
    public CandidateResponse getById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", id));
        return CandidateResponse.fromEntity(candidate);
    }

    @Transactional(readOnly = true)
    public List<CandidateResponse> getAll() {
        return candidateRepository.findAll().stream()
                .map(CandidateResponse::fromEntity)
                .toList();
    }

    /**
     * Paginated + filtered candidate listing.
     * All filter params are optional — pass null to skip a filter.
     */
    @Transactional(readOnly = true)
    public PagedResponse<CandidateResponse> getAllPaged(String name, String email,
                                                        String skill, Pageable pageable) {
        Specification<Candidate> spec = Specification
                .where(CandidateSpecification.hasName(name))
                .and(CandidateSpecification.hasEmail(email))
                .and(CandidateSpecification.hasSkill(skill));

        Page<Candidate> page = candidateRepository.findAll(spec, pageable);
        return PagedResponse.from(page, CandidateResponse::fromEntity);
    }

    public CandidateResponse update(Long id, CandidateRequest request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", id));

        candidate.setFullName(request.getFullName());
        candidate.setEmail(request.getEmail());
        candidate.setPhone(request.getPhone());
        candidate.setResumeUrl(request.getResumeUrl());
        candidate.setSkills(request.getSkills());

        return CandidateResponse.fromEntity(candidateRepository.save(candidate));
    }

    public void delete(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", id));
        candidateRepository.delete(candidate);
    }
}
