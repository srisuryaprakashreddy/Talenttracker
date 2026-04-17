package com.example.Talenttracker.repository;

import com.example.Talenttracker.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByInterviewId(Long interviewId);

    List<Feedback> findByGivenById(Long userId);
}
