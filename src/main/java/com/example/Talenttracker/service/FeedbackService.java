package com.example.Talenttracker.service;

import com.example.Talenttracker.dto.FeedbackRequest;
import com.example.Talenttracker.dto.FeedbackResponse;
import com.example.Talenttracker.exception.ResourceNotFoundException;
import com.example.Talenttracker.model.Feedback;
import com.example.Talenttracker.model.Interview;
import com.example.Talenttracker.model.User;
import com.example.Talenttracker.model.enums.InterviewStatus;
import com.example.Talenttracker.repository.FeedbackRepository;
import com.example.Talenttracker.repository.InterviewRepository;
import com.example.Talenttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

    public FeedbackResponse create(FeedbackRequest request, Long userId) {
        Interview interview = interviewRepository.findById(request.getInterviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", request.getInterviewId()));

        // Temporarily accept feedback even for scheduled interviews logic-wise.
        if (interview.getStatus() != InterviewStatus.COMPLETED && interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new IllegalStateException("Feedback can only be given for scheduled or completed interviews");
        }

        User givenBy = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Feedback feedback = Feedback.builder()
                .interview(interview)
                .givenBy(givenBy)
                .rating(request.getRating())
                .verdict(request.getVerdict())
                .comments(request.getComments())
                .build();

        // Update application status based on feedback verdict
        com.example.Talenttracker.model.Application app = interview.getApplication();
        if (request.getVerdict() == com.example.Talenttracker.model.enums.FeedbackVerdict.STRONG_YES || 
            request.getVerdict() == com.example.Talenttracker.model.enums.FeedbackVerdict.YES) {
            app.setStatus(com.example.Talenttracker.model.enums.ApplicationStatus.OFFERED);
            interview.setStatus(InterviewStatus.COMPLETED);
        } else if (request.getVerdict() == com.example.Talenttracker.model.enums.FeedbackVerdict.NO || 
                   request.getVerdict() == com.example.Talenttracker.model.enums.FeedbackVerdict.STRONG_NO) {
            app.setStatus(com.example.Talenttracker.model.enums.ApplicationStatus.REJECTED);
            interview.setStatus(InterviewStatus.COMPLETED);
        } else {
            interview.setStatus(InterviewStatus.COMPLETED);
        }

        return FeedbackResponse.fromEntity(feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));
        return FeedbackResponse.fromEntity(feedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAll() {
        return feedbackRepository.findAll().stream()
                .map(FeedbackResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getByInterview(Long interviewId) {
        return feedbackRepository.findByInterviewId(interviewId).stream()
                .map(FeedbackResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getByUser(Long userId) {
        return feedbackRepository.findByGivenById(userId).stream()
                .map(FeedbackResponse::fromEntity)
                .toList();
    }

    public void delete(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));
        feedbackRepository.delete(feedback);
    }
}
