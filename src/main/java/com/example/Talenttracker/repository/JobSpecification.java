package com.example.Talenttracker.repository;

import com.example.Talenttracker.model.Job;
import com.example.Talenttracker.model.enums.JobStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Dynamic query filters for Job entity.
 * Combine with .and() for multi-filter queries.
 */
public class JobSpecification {

    private JobSpecification() {}

    public static Specification<Job> hasTitle(String keyword) {
        return (root, query, cb) ->
                keyword == null ? null : cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Job> hasStatus(JobStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Job> hasDepartment(String department) {
        return (root, query, cb) ->
                department == null ? null : cb.equal(cb.lower(root.get("department")), department.toLowerCase());
    }

    public static Specification<Job> hasLocation(String location) {
        return (root, query, cb) ->
                location == null ? null : cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<Job> postedBy(Long recruiterId) {
        return (root, query, cb) ->
                recruiterId == null ? null : cb.equal(root.get("postedBy").get("id"), recruiterId);
    }
}
