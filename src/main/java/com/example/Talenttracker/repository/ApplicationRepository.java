package com.example.Talenttracker.repository;

import com.example.Talenttracker.model.Application;
import com.example.Talenttracker.model.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByCandidateId(Long candidateId);

    List<Application> findByJobId(Long jobId);

    List<Application> findByStatus(ApplicationStatus status);

    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);

    long countByStatus(ApplicationStatus status);

    List<Application> findTop5ByOrderByAppliedAtDesc();
}
