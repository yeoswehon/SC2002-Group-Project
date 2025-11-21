package services.impl;

import entities.application.Application;
import entities.application.ApplicationStatus;
import entities.approval.ApprovalItem;
import entities.approval.Type;
import entities.common.Visibility;
import entities.posting.InternshipPosting;
import entities.posting.Level;
import entities.posting.PostingStatus;
import entities.users.CompanyRep;
import entities.users.Student;
import kernel.Result;
import repositories.ApprovalQueue;
import repositories.ApplicationRepository;
import repositories.PostingRepository;
import repositories.UserRepository;
import services.ApplicationService;
import util.Clock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Concrete implementation of Application Service
 */
public final class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository apps;
    private final PostingRepository postings;
    private final UserRepository users;
    private final ApprovalQueue approvals;
    private final Clock clock;

    public ApplicationServiceImpl(ApplicationRepository apps,
                                  PostingRepository postings,
                                  UserRepository users,
                                  ApprovalQueue approvals,
                                  Clock clock) {
        this.apps = apps;
        this.postings = postings;
        this.users = users;
        this.approvals = approvals;
        this.clock = clock;
    }

    @Override public Result<Application> apply(Student s, String postingId) {
        Optional<InternshipPosting> p = postings.findById(postingId);
        if (p.isEmpty()) return Result.fail("Posting not found");

        InternshipPosting post = p.get();
        LocalDate today = clock.today();

        if (today.isBefore(post.getOpenDate()) || today.isAfter(post.getCloseDate()))
            return Result.fail("Application window closed");
        if (post.getStatus() != PostingStatus.APPROVED) return Result.fail("Posting not approved");
        if (post.getVisibility() != Visibility.PUBLIC) return Result.fail("Posting is hidden");
        if (!eligible(s, post.getLevel())) return Result.fail("Not eligible for this level");

        long active = apps.findByStudent(s.getId()).stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING || a.getStatus() == ApplicationStatus.SUCCESSFUL)
                .count();
        if (active >= 3) return Result.fail("You can have at most 3 active applications");

        Application a = new Application(s.getId(), postingId, today);
        apps.save(a);
        return Result.ok(a);
    }

    @Override public List<Application> listForStudent(String studentId) {
        return apps.findByStudent(studentId);
    }

    @Override public List<Application> listForPosting(String postingId) {
        return apps.findByPosting(postingId);
    }
    
    @Override public Result<Void> reviewByCompany(CompanyRep rep, String applicationId, boolean approve) {
        Optional<Application> a = apps.findById(applicationId);
        if (a.isEmpty()) return Result.fail("Application not found");
        Optional<InternshipPosting> p = postings.findById(a.get().getPostingId());
        if (p.isEmpty()) return Result.fail("Posting missing");
        if (!p.get().getCompanyRepId().equals(rep.getId())) return Result.fail("Not your posting");

        a.get().setStatus(approve ? ApplicationStatus.SUCCESSFUL : ApplicationStatus.UNSUCCESSFUL, clock.today());
        apps.save(a.get());
        return Result.ok();
    }

    @Override public Result<Void> acceptOffer(String studentId, String applicationId) {
        Optional<Application> a = apps.findById(applicationId);
        if (a.isEmpty()) return Result.fail("Application not found");
        if (!a.get().getStudentId().equals(studentId)) return Result.fail("Not your application");
        if (a.get().getStatus() != ApplicationStatus.SUCCESSFUL) return Result.fail("You can only accept successful offers");

        Optional<InternshipPosting> p = postings.findById(a.get().getPostingId());
        if (p.isEmpty()) return Result.fail("Posting missing");
        if (!p.get().hasCapacity()) return Result.fail("Posting has no remaining slots");

        for (Application other : apps.findByStudent(studentId)) {
            if (!other.getId().equals(a.get().getId()) &&
                (other.getStatus() == ApplicationStatus.PENDING || other.getStatus() == ApplicationStatus.SUCCESSFUL)) {
                other.setStatus(ApplicationStatus.WITHDRAWN, clock.today());
                apps.save(other);
            }
        }
        a.get().setStatus(ApplicationStatus.ACCEPTED, clock.today());

        p.get().confirmOne();
        postings.save(p.get());
        return Result.ok();
    }

    @Override public Result<Void> requestWithdrawal(String studentId, String applicationId, String reason) {
        Optional<Application> a = apps.findById(applicationId);
        if (a.isEmpty()) return Result.fail("Application not found");
        if (!a.get().getStudentId().equals(studentId)) return Result.fail("Not your application");
        if (a.get().getStatus() != ApplicationStatus.PENDING &&
            a.get().getStatus() != ApplicationStatus.SUCCESSFUL &&
            a.get().getStatus() != ApplicationStatus.ACCEPTED) {
            return Result.fail("Only pending/successful applications can be withdrawn");
        }

        a.get().setStatus(ApplicationStatus.WITHDRAW_REQUESTED, clock.today());
        apps.save(a.get());
        approvals.submit(new ApprovalItem(Type.WITHDRAWAL, a.get().getId(), studentId, reason));
        return Result.ok();
    }

    private boolean eligible(Student s, Level level) {
        if (s.getYear() <= 2) return level == Level.BASIC;
        return true;
    }
}