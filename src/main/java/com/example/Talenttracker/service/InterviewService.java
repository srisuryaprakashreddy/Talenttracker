package com.example.Talenttracker.service;

import com.example.Talenttracker.dto.InterviewRequest;
import com.example.Talenttracker.dto.InterviewResponse;
import com.example.Talenttracker.exception.ResourceNotFoundException;
import com.example.Talenttracker.model.Application;
import com.example.Talenttracker.model.Interview;
import com.example.Talenttracker.model.User;
import com.example.Talenttracker.model.enums.InterviewStatus;
import com.example.Talenttracker.repository.ApplicationRepository;
import com.example.Talenttracker.repository.InterviewRepository;
import com.example.Talenttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public InterviewResponse schedule(InterviewRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", request.getApplicationId()));

        User interviewer = userRepository.findById(request.getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getInterviewerId()));

        Interview interview = Interview.builder()
                .application(application)
                .interviewer(interviewer)
                .scheduledAt(request.getScheduledAt())
                .round(request.getRound() != null ? request.getRound() : 1)
                .status(InterviewStatus.SCHEDULED)
                .build();

        return InterviewResponse.fromEntity(interviewRepository.save(interview));
    }

    @Transactional(readOnly = true)
    public InterviewResponse getById(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", id));
        return InterviewResponse.fromEntity(interview);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getByApplication(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId).stream()
                .map(InterviewResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getAll() {
        return interviewRepository.findAll().stream()
                .map(InterviewResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return interviewRepository.findByScheduledAtBetween(start, end).stream()
                .map(InterviewResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getByInterviewer(Long interviewerId) {
        return interviewRepository.findByInterviewerId(interviewerId).stream()
                .map(InterviewResponse::fromEntity)
                .toList();
    }

    public InterviewResponse updateStatus(Long id, InterviewStatus status) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", id));

        interview.setStatus(status);
        return InterviewResponse.fromEntity(interviewRepository.save(interview));
    }
}
