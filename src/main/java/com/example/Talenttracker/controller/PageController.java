package com.example.Talenttracker.controller;

import com.example.Talenttracker.dto.*;
import com.example.Talenttracker.model.enums.ApplicationStatus;
import com.example.Talenttracker.model.enums.FeedbackVerdict;
import com.example.Talenttracker.model.enums.InterviewStatus;
import com.example.Talenttracker.model.enums.JobStatus;
import com.example.Talenttracker.model.enums.Role;
import com.example.Talenttracker.repository.UserRepository;
import com.example.Talenttracker.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serves all Thymeleaf views with real backend data.
 */
@Controller
@RequiredArgsConstructor
public class PageController {

    private final JobService jobService;
    private final CandidateService candidateService;
    private final ApplicationService applicationService;
    private final InterviewService interviewService;
    private final FeedbackService feedbackService;
    private final UserService userService;
    private final UserRepository userRepository;

    // ══════════════════════════════════════════════════════════
    //  LOGIN
    // ══════════════════════════════════════════════════════════

    @GetMapping("/login")
    public String loginPage() {
        return "pages/login";
    }

    // ══════════════════════════════════════════════════════════
    //  DASHBOARD
    // ══════════════════════════════════════════════════════════

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Authentication auth) {
        setupLayout(model, auth, "Dashboard — TalentTrack Lite", "pages/dashboard :: content");

        // Stat counts
        model.addAttribute("totalJobs", jobService.getAll().size());
        model.addAttribute("totalCandidates", candidateService.getAll().size());
        model.addAttribute("totalApplications", applicationService.getAll().size());

        boolean isInterviewer = false;
        Long userId = null;
        if (auth != null) {
            isInterviewer = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_INTERVIEWER"));
            userId = userRepository.findByEmail(auth.getName()).map(com.example.Talenttracker.model.User::getId).orElse(null);
        }

        // Today's interviews
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<InterviewResponse> todayInterviews = interviewService.getByDateRange(startOfDay, endOfDay);
        
        if (isInterviewer && userId != null) {
            final Long currentUserId = userId; // effectively final for lambda
            todayInterviews = todayInterviews.stream()
                .filter(i -> currentUserId.equals(i.getInterviewerId()))
                .toList();
            model.addAttribute("totalJobs", 0);
            model.addAttribute("totalCandidates", 0);
            model.addAttribute("totalApplications", 0);
        }

        model.addAttribute("interviewsToday", todayInterviews.size());
        model.addAttribute("todayInterviews", todayInterviews);

        // Application status counts
        model.addAttribute("appliedCount", applicationService.countByStatus(ApplicationStatus.APPLIED));
        model.addAttribute("shortlistedCount", applicationService.countByStatus(ApplicationStatus.SHORTLISTED));
        model.addAttribute("interviewingCount", applicationService.countByStatus(ApplicationStatus.INTERVIEWING));
        model.addAttribute("offeredCount", applicationService.countByStatus(ApplicationStatus.OFFERED));
        model.addAttribute("rejectedCount", applicationService.countByStatus(ApplicationStatus.REJECTED));
        model.addAttribute("hiredCount", applicationService.countByStatus(ApplicationStatus.HIRED));

        // Recent applications
        List<ApplicationResponse> recentApps = applicationService.getRecent();
        model.addAttribute("recentApplications", recentApps);

        // User name for welcome message
        if (auth != null) {
            userRepository.findByEmail(auth.getName()).ifPresent(user ->
                    model.addAttribute("userName", user.getFullName().split(" ")[0])
            );
        }

        return "layouts/main";
    }

    // ══════════════════════════════════════════════════════════
    //  JOBS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/jobs")
    public String jobs(Model model, Authentication auth,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String department,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {

        setupLayout(model, auth, "Jobs — TalentTrack Lite", "pages/jobs :: content");

        JobStatus jobStatus = null;
        if (status != null && !status.isEmpty()) {
            try { jobStatus = JobStatus.valueOf(status); } catch (Exception ignored) {}
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PagedResponse<JobResponse> result = jobService.getAllPaged(keyword, jobStatus, department, null, null, pageable);

        model.addAttribute("jobs", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);
        model.addAttribute("departmentFilter", department);

        return "layouts/main";
    }

    @GetMapping("/jobs/new")
    public String newJobForm(Model model, Authentication auth) {
        setupLayout(model, auth, "Add Job — TalentTrack Lite", "pages/jobs-new :: content");
        model.addAttribute("jobRequest", new JobRequest());
        return "layouts/main";
    }

    @PostMapping("/jobs/new")
    public String createJob(@ModelAttribute("jobRequest") JobRequest request, Authentication auth) {
        if (auth != null) {
            userRepository.findByEmail(auth.getName()).ifPresent(user -> {
                jobService.create(request, user.getId());
            });
        }
        return "redirect:/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes ra) {
        try {
            jobService.delete(id);
            ra.addFlashAttribute("success", "Job deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete job: " + e.getMessage());
        }
        return "redirect:/jobs";
    }

    // ══════════════════════════════════════════════════════════
    //  CANDIDATES
    // ══════════════════════════════════════════════════════════

    @GetMapping("/candidates")
    public String candidates(Model model, Authentication auth,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String skill,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {

        setupLayout(model, auth, "Candidates — TalentTrack Lite", "pages/candidates :: content");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PagedResponse<CandidateResponse> result = candidateService.getAllPaged(name, null, skill, pageable);

        model.addAttribute("candidates", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("nameFilter", name);
        model.addAttribute("skillFilter", skill);

        return "layouts/main";
    }

    @GetMapping("/candidates/new")
    public String newCandidateForm(Model model, Authentication auth) {
        setupLayout(model, auth, "Add Candidate — TalentTrack Lite", "pages/candidates-new :: content");
        model.addAttribute("candidateRequest", new CandidateRequest());
        return "layouts/main";
    }

    @PostMapping("/candidates/new")
    public String createCandidate(@ModelAttribute("candidateRequest") CandidateRequest request) {
        candidateService.create(request);
        return "redirect:/candidates";
    }

    @PostMapping("/candidates/{id}/delete")
    public String deleteCandidate(@PathVariable Long id, RedirectAttributes ra) {
        try {
            candidateService.delete(id);
            ra.addFlashAttribute("success", "Candidate deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete candidate: " + e.getMessage());
        }
        return "redirect:/candidates";
    }

    // ══════════════════════════════════════════════════════════
    //  APPLICATIONS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/applications")
    public String applications(Model model, Authentication auth,
                               @RequestParam(required = false) String status) {
        setupLayout(model, auth, "Applications — TalentTrack Lite", "pages/applications :: content");

        List<ApplicationResponse> apps = applicationService.getAll();
        if (status != null && !status.isEmpty()) {
            try {
                ApplicationStatus appStatus = ApplicationStatus.valueOf(status);
                apps = apps.stream().filter(a -> a.getStatus() == appStatus).toList();
            } catch (Exception ignored) {}
        }
        model.addAttribute("applications", apps);
        model.addAttribute("statusFilter", status);

        return "layouts/main";
    }

    @GetMapping("/applications/new")
    public String newApplicationForm(Model model, Authentication auth) {
        setupLayout(model, auth, "Assign Candidate — TalentTrack Lite", "pages/applications-new :: content");
        model.addAttribute("applicationRequest", new ApplicationRequest());
        model.addAttribute("candidates", candidateService.getAll());
        model.addAttribute("jobs", jobService.getAll());
        return "layouts/main";
    }

    @PostMapping("/applications/new")
    public String createApplication(@ModelAttribute("applicationRequest") ApplicationRequest request) {
        applicationService.create(request);
        return "redirect:/applications";
    }

    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
                                          @RequestParam ApplicationStatus status) {
        applicationService.updateStatus(id, status);
        return "redirect:/applications";
    }

    @PostMapping("/applications/{id}/delete")
    public String deleteApplication(@PathVariable Long id, RedirectAttributes ra) {
        try {
            applicationService.delete(id);
            ra.addFlashAttribute("success", "Application deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete application: " + e.getMessage());
        }
        return "redirect:/applications";
    }

    // ══════════════════════════════════════════════════════════
    //  INTERVIEWS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/interviews")
    public String interviews(Model model, Authentication auth,
                             @RequestParam(required = false) String status) {
        setupLayout(model, auth, "Interviews — TalentTrack Lite", "pages/interviews :: content");

        boolean isInterviewer = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INTERVIEWER"));
                
        List<InterviewResponse> interviews;
        if (isInterviewer) {
            Long userId = userRepository.findByEmail(auth.getName()).get().getId();
            interviews = interviewService.getByInterviewer(userId);
        } else {
            interviews = interviewService.getAll();
        }
        if (status != null && !status.isEmpty()) {
            try {
                InterviewStatus intStatus = InterviewStatus.valueOf(status);
                interviews = interviews.stream().filter(i -> i.getStatus() == intStatus).toList();
            } catch (Exception ignored) {}
        }
        model.addAttribute("interviews", interviews);
        model.addAttribute("statusFilter", status);

        return "layouts/main";
    }

    @GetMapping("/interviews/new")
    public String newInterviewForm(Model model, Authentication auth) {
        setupLayout(model, auth, "Schedule Interview — TalentTrack Lite", "pages/interviews-new :: content");
        model.addAttribute("interviewRequest", new InterviewRequest());
        model.addAttribute("applications", applicationService.getAll());
        model.addAttribute("interviewers", userService.getUsersByRole(Role.INTERVIEWER));
        return "layouts/main";
    }

    @PostMapping("/interviews/new")
    public String scheduleInterview(@ModelAttribute("interviewRequest") InterviewRequest request) {
        interviewService.schedule(request);
        return "redirect:/interviews";
    }

    @PostMapping("/interviews/{id}/delete")
    public String deleteInterview(@PathVariable Long id, RedirectAttributes ra) {
        try {
            interviewService.delete(id);
            ra.addFlashAttribute("success", "Interview deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete interview: " + e.getMessage());
        }
        return "redirect:/interviews";
    }

    // ══════════════════════════════════════════════════════════
    //  FEEDBACK
    // ══════════════════════════════════════════════════════════

    @GetMapping("/feedback")
    public String feedback(Model model, Authentication auth,
                           @RequestParam(required = false) String verdict) {
        setupLayout(model, auth, "Feedback — TalentTrack Lite", "pages/feedback :: content");

        boolean isInterviewer = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INTERVIEWER"));
        
        List<FeedbackResponse> feedbacks;
        if (isInterviewer) {
            Long userId = userRepository.findByEmail(auth.getName()).get().getId();
            feedbacks = feedbackService.getByUser(userId);
        } else {
            feedbacks = feedbackService.getAll();
        }
        if (verdict != null && !verdict.isEmpty()) {
            try {
                FeedbackVerdict fv = FeedbackVerdict.valueOf(verdict);
                feedbacks = feedbacks.stream().filter(f -> f.getVerdict() == fv).toList();
            } catch (Exception ignored) {}
        }
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("verdictFilter", verdict);

        return "layouts/main";
    }

    @GetMapping("/feedback/new")
    public String newFeedbackForm(Model model, Authentication auth) {
        setupLayout(model, auth, "Submit Feedback — TalentTrack Lite", "pages/feedback-new :: content");
        model.addAttribute("feedbackRequest", new FeedbackRequest());
        boolean isInterviewer = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INTERVIEWER"));
        
        List<InterviewResponse> dropdownInterviews;
        if (isInterviewer) {
            Long userId = userRepository.findByEmail(auth.getName()).get().getId();
            dropdownInterviews = interviewService.getByInterviewer(userId);
        } else {
            dropdownInterviews = interviewService.getAll();
        }
        model.addAttribute("interviews", dropdownInterviews);
        return "layouts/main";
    }

    @PostMapping("/feedback/new")
    public String submitFeedback(@ModelAttribute("feedbackRequest") FeedbackRequest request, Authentication auth) {
        String email = auth.getName();
        Long givenById = userRepository.findByEmail(email).get().getId();
        feedbackService.create(request, givenById);
        return "redirect:/feedback";
    }

    @PostMapping("/feedback/{id}/delete")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes ra) {
        try {
            feedbackService.delete(id);
            ra.addFlashAttribute("success", "Feedback deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete feedback: " + e.getMessage());
        }
        return "redirect:/feedback";
    }

    // ══════════════════════════════════════════════════════════
    //  REPORTS (dynamic data)
    // ══════════════════════════════════════════════════════════

    @GetMapping("/reports")
    public String reports(Model model, Authentication auth) {
        setupLayout(model, auth, "Reports & Analytics — TalentTrack Lite", "pages/reports :: content");

        // Counts
        long totalJobs = jobService.getAll().size();
        long totalApps = applicationService.getAll().size();
        long totalInterviews = interviewService.getAll().size();
        long totalFeedback = feedbackService.getAll().size();
        long totalCandidates = candidateService.getAll().size();

        model.addAttribute("jobsCount", totalJobs);
        model.addAttribute("appsCount", totalApps);
        model.addAttribute("interviewsCount", totalInterviews);
        model.addAttribute("feedbackCount", totalFeedback);
        model.addAttribute("candidatesCount", totalCandidates);

        // Status breakdowns
        long applied = applicationService.countByStatus(ApplicationStatus.APPLIED);
        long shortlisted = applicationService.countByStatus(ApplicationStatus.SHORTLISTED);
        long interviewing = applicationService.countByStatus(ApplicationStatus.INTERVIEWING);
        long offered = applicationService.countByStatus(ApplicationStatus.OFFERED);
        long rejected = applicationService.countByStatus(ApplicationStatus.REJECTED);
        long hired = applicationService.countByStatus(ApplicationStatus.HIRED);

        model.addAttribute("appliedCount", applied);
        model.addAttribute("shortlistedCount", shortlisted);
        model.addAttribute("interviewingCount", interviewing);
        model.addAttribute("offeredCount", offered);
        model.addAttribute("rejectedCount", rejected);
        model.addAttribute("hiredCount", hired);

        // Percentages for progress bars (avoid division by zero)
        model.addAttribute("interviewPct", totalApps > 0 ? (interviewing * 100 / totalApps) : 0);
        model.addAttribute("offerPct", totalApps > 0 ? ((offered + hired) * 100 / totalApps) : 0);
        model.addAttribute("rejectPct", totalApps > 0 ? (rejected * 100 / totalApps) : 0);

        return "layouts/main";
    }

    // ══════════════════════════════════════════════════════════
    //  USERS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/users")
    public String users(Model model, Authentication auth) {
        setupLayout(model, auth, "Users — TalentTrack Lite", "pages/users :: content");
        model.addAttribute("users", userService.getAllUsers());
        return "layouts/main";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model, Authentication auth) {
        setupLayout(model, auth, "Add User — TalentTrack Lite", "pages/users-new :: content");
        model.addAttribute("userRequest", new UserRequest());
        return "layouts/main";
    }

    @PostMapping("/users/new")
    public String createUser(@ModelAttribute("userRequest") UserRequest request) {
        userService.createUser(request);
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.deleteUser(id);
            ra.addFlashAttribute("success", "User deactivated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/users";
    }

    // ══════════════════════════════════════════════════════════
    //  DETAIL VIEWS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/jobs/{id}")
    public String jobDetails(@PathVariable Long id, Model model, Authentication auth) {
        JobResponse job = jobService.getById(id);
        setupLayout(model, auth, "Job Details — TalentTrack Lite", "pages/job-details :: content");
        model.addAttribute("job", job);
        return "layouts/main";
    }

    @GetMapping("/candidates/{id}")
    public String candidateDetails(@PathVariable Long id, Model model, Authentication auth) {
        CandidateResponse candidate = candidateService.getById(id);
        setupLayout(model, auth, "Candidate Details — TalentTrack Lite", "pages/candidate-details :: content");
        model.addAttribute("candidate", candidate);
        return "layouts/main";
    }

    @GetMapping("/applications/{id}")
    public String applicationDetails(@PathVariable Long id, Model model, Authentication auth) {
        ApplicationResponse app = applicationService.getById(id);
        setupLayout(model, auth, "Application Details — TalentTrack Lite", "pages/application-details :: content");
        model.addAttribute("app", app);
        return "layouts/main";
    }

    @GetMapping("/interviews/{id}")
    public String interviewDetails(@PathVariable Long id, Model model, Authentication auth) {
        InterviewResponse interview = interviewService.getById(id);
        setupLayout(model, auth, "Interview Details — TalentTrack Lite", "pages/interview-details :: content");
        model.addAttribute("interview", interview);
        return "layouts/main";
    }

    @GetMapping("/feedback/{id}")
    public String feedbackDetails(@PathVariable Long id, Model model, Authentication auth) {
        FeedbackResponse fb = feedbackService.getById(id);
        setupLayout(model, auth, "Feedback Details — TalentTrack Lite", "pages/feedback-details :: content");
        model.addAttribute("feedback", fb);
        return "layouts/main";
    }

    // ══════════════════════════════════════════════════════════
    //  SHARED LAYOUT SETUP
    // ══════════════════════════════════════════════════════════

    private void setupLayout(Model model, Authentication auth, String title, String contentFragment) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("contentFragment", contentFragment);

        if (auth != null) {
            String email = auth.getName();
            String role = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .map(r -> r.replace("ROLE_", ""))
                    .orElse("User");

            model.addAttribute("userRole", role);
            model.addAttribute("userInitial", email.substring(0, 1).toUpperCase());

            // Full name for navbar
            userRepository.findByEmail(email).ifPresent(user ->
                    model.addAttribute("userFullName", user.getFullName())
            );
        }
    }
}
