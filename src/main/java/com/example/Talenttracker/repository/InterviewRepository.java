package com.example.Talenttracker.repository;

import com.example.Talenttracker.model.Interview;
import com.example.Talenttracker.model.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationId(Long applicationId);

    List<Interview> findByInterviewerId(Long interviewerId);

    List<Interview> findByStatus(InterviewStatus status);

    List<Interview> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
}
