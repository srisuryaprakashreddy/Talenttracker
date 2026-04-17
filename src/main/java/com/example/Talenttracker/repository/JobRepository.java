package com.example.Talenttracker.repository;

import com.example.Talenttracker.model.Job;
import com.example.Talenttracker.model.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    List<Job> findByPostedById(Long recruiterId);

    List<Job> findByStatus(JobStatus status);

    List<Job> findByDepartment(String department);

    List<Job> findByTitleContainingIgnoreCase(String keyword);

    Page<Job> findByPostedById(Long recruiterId, Pageable pageable);
}
