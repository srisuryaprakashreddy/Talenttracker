package com.example.Talenttracker.config;

import com.example.Talenttracker.model.*;
import com.example.Talenttracker.model.enums.*;
import com.example.Talenttracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Seeds dummy data on startup for frontend development.
 * Only inserts if the database is empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final FeedbackRepository feedbackRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded — performing active fix for existing users.");
            userRepository.findAll().forEach(u -> {
                if (!u.isActive()) {
                    u.setActive(true);
                    userRepository.save(u);
                }
            });
            return;
        }

        log.info("Seeding dummy data...");

        // ── Users ───────────────────────────────────────────
        User admin = userRepository.save(User.builder()
                .fullName("Admin User")
                .email("admin@talenttrack.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.ADMIN)
                .build());

        User recruiter = userRepository.save(User.builder()
                .fullName("Sarah Johnson")
                .email("recruiter@talenttrack.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.RECRUITER)
                .build());

        User interviewer1 = userRepository.save(User.builder()
                .fullName("David Park")
                .email("interviewer@talenttrack.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.INTERVIEWER)
                .build());

        User interviewer2 = userRepository.save(User.builder()
                .fullName("Jessica Williams")
                .email("jessica@talenttrack.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.INTERVIEWER)
                .build());

        // ── Jobs ────────────────────────────────────────────
        Job job1 = jobRepository.save(Job.builder()
                .title("Senior Frontend Developer")
                .description("Build modern web interfaces using React and TypeScript")
                .location("Remote")
                .department("Engineering")
                .status(JobStatus.OPEN)
                .postedBy(recruiter)
                .build());

        Job job2 = jobRepository.save(Job.builder()
                .title("Product Manager")
                .description("Lead product strategy and roadmap for enterprise clients")
                .location("New York, NY")
                .department("Product")
                .status(JobStatus.OPEN)
                .postedBy(recruiter)
                .build());

        Job job3 = jobRepository.save(Job.builder()
                .title("UX Designer")
                .description("Design intuitive user experiences for web and mobile")
                .location("San Francisco, CA")
                .department("Design")
                .status(JobStatus.OPEN)
                .postedBy(recruiter)
                .build());

        Job job4 = jobRepository.save(Job.builder()
                .title("Backend Engineer")
                .description("Develop scalable microservices with Spring Boot and Java")
                .location("Remote")
                .department("Engineering")
                .status(JobStatus.OPEN)
                .postedBy(recruiter)
                .build());

        Job job5 = jobRepository.save(Job.builder()
                .title("Data Analyst Intern")
                .description("Analyze recruitment data and generate insights")
                .location("Chicago, IL")
                .department("Analytics")
                .status(JobStatus.CLOSED)
                .postedBy(recruiter)
                .build());

        // ── Candidates ──────────────────────────────────────
        Candidate c1 = candidateRepository.save(Candidate.builder()
                .fullName("Alex Thompson")
                .email("alex@example.com")
                .phone("+1-555-0101")
                .skills("React, TypeScript, Node.js, CSS")
                .build());

        Candidate c2 = candidateRepository.save(Candidate.builder()
                .fullName("Maria Garcia")
                .email("maria@example.com")
                .phone("+1-555-0102")
                .skills("Product Strategy, Agile, Jira, SQL")
                .build());

        Candidate c3 = candidateRepository.save(Candidate.builder()
                .fullName("James Kim")
                .email("james@example.com")
                .phone("+1-555-0103")
                .skills("Figma, Sketch, User Research, Prototyping")
                .build());

        Candidate c4 = candidateRepository.save(Candidate.builder()
                .fullName("Ryan O'Brien")
                .email("ryan@example.com")
                .phone("+1-555-0104")
                .skills("Java, Spring Boot, Docker, Kubernetes")
                .build());

        Candidate c5 = candidateRepository.save(Candidate.builder()
                .fullName("Emily Chen")
                .email("emily@example.com")
                .phone("+1-555-0105")
                .skills("Python, SQL, Tableau, Data Analytics")
                .build());

        Candidate c6 = candidateRepository.save(Candidate.builder()
                .fullName("Priya Sharma")
                .email("priya@example.com")
                .phone("+1-555-0106")
                .skills("Vue.js, Angular, GraphQL, REST APIs")
                .build());

        // ── Applications ────────────────────────────────────
        Application app1 = applicationRepository.save(Application.builder()
                .job(job1).candidate(c1).status(ApplicationStatus.INTERVIEWING)
                .appliedAt(LocalDateTime.of(2026, 3, 16, 9, 0)).build());

        Application app2 = applicationRepository.save(Application.builder()
                .job(job2).candidate(c2).status(ApplicationStatus.SHORTLISTED)
                .appliedAt(LocalDateTime.of(2026, 3, 21, 10, 0)).build());

        Application app3 = applicationRepository.save(Application.builder()
                .job(job3).candidate(c3).status(ApplicationStatus.INTERVIEWING)
                .appliedAt(LocalDateTime.of(2026, 4, 2, 14, 0)).build());

        Application app4 = applicationRepository.save(Application.builder()
                .job(job1).candidate(c4).status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.of(2026, 4, 5, 11, 0)).build());

        Application app5 = applicationRepository.save(Application.builder()
                .job(job4).candidate(c5).status(ApplicationStatus.OFFERED)
                .appliedAt(LocalDateTime.of(2026, 3, 10, 8, 0)).build());

        Application app6 = applicationRepository.save(Application.builder()
                .job(job1).candidate(c6).status(ApplicationStatus.REJECTED)
                .appliedAt(LocalDateTime.of(2026, 3, 12, 16, 0)).build());

        Application app7 = applicationRepository.save(Application.builder()
                .job(job4).candidate(c1).status(ApplicationStatus.SHORTLISTED)
                .appliedAt(LocalDateTime.of(2026, 4, 10, 9, 30)).build());

        // ── Interviews ──────────────────────────────────────
        Interview i1 = interviewRepository.save(Interview.builder()
                .application(app1).interviewer(interviewer1)
                .scheduledAt(LocalDateTime.of(2026, 4, 15, 19, 30))
                .round(1).status(InterviewStatus.SCHEDULED).build());

        Interview i2 = interviewRepository.save(Interview.builder()
                .application(app3).interviewer(interviewer2)
                .scheduledAt(LocalDateTime.of(2026, 4, 15, 15, 30))
                .round(1).status(InterviewStatus.SCHEDULED).build());

        Interview i3 = interviewRepository.save(Interview.builder()
                .application(app4).interviewer(interviewer1)
                .scheduledAt(LocalDateTime.of(2026, 4, 10, 20, 30))
                .round(1).status(InterviewStatus.COMPLETED).build());

        Interview i4 = interviewRepository.save(Interview.builder()
                .application(app1).interviewer(interviewer2)
                .scheduledAt(LocalDateTime.of(2026, 4, 16, 16, 30))
                .round(2).status(InterviewStatus.SCHEDULED)
                .notes("Second round - phone screen").build());

        // ── Feedback ────────────────────────────────────────
        feedbackRepository.save(Feedback.builder()
                .interview(i3).givenBy(interviewer1)
                .rating(4).verdict(FeedbackVerdict.YES)
                .comments("Strong problem-solving skills. Good cultural fit.")
                .build());

        feedbackRepository.save(Feedback.builder()
                .interview(i3).givenBy(interviewer2)
                .rating(3).verdict(FeedbackVerdict.NEUTRAL)
                .comments("Decent technical skills but needs more experience with distributed systems.")
                .build());

        log.info("Seed data loaded: {} users, {} jobs, {} candidates, {} applications, {} interviews, {} feedbacks",
                userRepository.count(), jobRepository.count(), candidateRepository.count(),
                applicationRepository.count(), interviewRepository.count(), feedbackRepository.count());
    }
}
