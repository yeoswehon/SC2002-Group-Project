package services.impl;

import entities.application.Application;
import entities.application.ApplicationStatus;
import entities.posting.InternshipPosting;
import entities.posting.PostingStatus;
import entities.posting.Major;   
import entities.users.Student;
import repositories.ApplicationRepository;
import repositories.PostingRepository;
import repositories.UserRepository;
import services.ReportingService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Concrete implementation of Reporting Service
 */
public final class ReportingServiceImpl implements ReportingService {

    private final UserRepository users;
    private final PostingRepository postings;
    private final ApplicationRepository apps;

    public ReportingServiceImpl(UserRepository users,
                                PostingRepository postings,
                                ApplicationRepository apps) {
        this.users = users;
        this.postings = postings;
        this.apps = apps;
    }

    @Override
    public Map<Major, Long> countApplicationsByMajor() {  
        return apps.findAll().stream()
                .collect(Collectors.groupingBy(
                        (Application a) -> users.findById(a.getStudentId())  
                                .filter(u -> u instanceof Student)
                                .map(u -> ((Student) u).getMajor())
                                .orElse(null),
                        Collectors.counting()));
    }

    @Override
    public List<InternshipPosting> listApprovedPostings() {
        return postings.findAll().stream()
                .filter(p -> p.getStatus() == PostingStatus.APPROVED)
                .collect(Collectors.toList());
    }

    @Override
    public List<Application> listSuccessfulApplications() {
        return apps.findAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL)
                .collect(Collectors.toList());
    }
}