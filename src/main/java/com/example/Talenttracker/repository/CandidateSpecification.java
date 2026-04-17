package com.example.Talenttracker.repository;

import com.example.Talenttracker.model.Candidate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Dynamic query filters for Candidate entity.
 */
public class CandidateSpecification {

    private CandidateSpecification() {}

    public static Specification<Candidate> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Candidate> hasEmail(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Candidate> hasSkill(String skill) {
        return (root, query, cb) ->
                skill == null ? null : cb.like(cb.lower(root.get("skills")), "%" + skill.toLowerCase() + "%");
    }
}
