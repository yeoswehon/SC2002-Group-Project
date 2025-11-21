package services.impl;

import entities.common.Visibility;
import entities.posting.InternshipPosting;
import entities.posting.Level;
import entities.posting.Major;
import entities.posting.PostingStatus;
import entities.users.CompanyRep;
import entities.users.Student;
import entities.approval.ApprovalItem;
import entities.approval.Type;
import kernel.Result;
import repositories.ApprovalQueue;
import repositories.PostingRepository;
import util.Clock;
import services.PostingService;
import settings.SessionSettings;
import settings.SessionSettings.PostingFilter;
import settings.SessionSettings.SortBy;
import java.util.Comparator;
import java.util.stream.Stream;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
/**
 * Concrete implementation of Posting Service
 */
public final class PostingServiceImpl implements PostingService {
    private final PostingRepository repo;
    private final ApprovalQueue approvals;
    private final Clock clock;

    public PostingServiceImpl(PostingRepository repo, ApprovalQueue approvals, Clock clock) {
        this.repo = repo;
        this.approvals = approvals;
        this.clock = clock;
    }

    @Override public List<InternshipPosting> listVisibleForStudent(Student s) {
        return repo.findVisibleByMajorAndWindow(Visibility.PUBLIC, s.getMajor(), clock.today());
    }

    @Override public Result<InternshipPosting> createDraft(CompanyRep owner, String title, String description,
                                                        Level level, Major major, LocalDate openDate,
                                                        LocalDate closeDate, int slots) {
        if (!owner.isApproved()) return Result.fail("Company representative not yet approved by staff");
        if (slots < 1 || slots > 10) return Result.fail("Slots must be between 1 and 10");
        if (openDate.isAfter(closeDate)) return Result.fail("Open date must be on/before close date");

        long existing = repo.findByCompany(owner.getId()).stream().count();

        if (existing >= 5) return Result.fail("Each representative may have at most 5 postings");

        InternshipPosting p = new InternshipPosting(owner.getId(), title, description, level, major, openDate, closeDate, slots);
        repo.save(p);
        return Result.ok(p);
    }

    @Override
    public Result<Void> updateDraft(CompanyRep owner, String postingId, String title, String description,
                                    Level level, Major major, LocalDate openDate, LocalDate closeDate, int slots) {
        var p = repo.findById(postingId);
        if (p.isEmpty()) return Result.fail("Posting not found");
        if (!p.get().getCompanyRepId().equals(owner.getId())) return Result.fail("Not your posting");
        if (p.get().getStatus() != PostingStatus.DRAFT && p.get().getStatus() != PostingStatus.REJECTED)
            return Result.fail("Only DRAFT/REJECTED postings can be edited");
        if (slots < 1 || slots > 10) return Result.fail("Slots must be between 1 and 10");
        if (openDate.isAfter(closeDate)) return Result.fail("Open date must be on/before close date");

        p.get().setTitle(title);
        p.get().setDescription(description);
        p.get().setLevel(level);
        p.get().setMajor(major);
        p.get().setDates(openDate, closeDate);
        p.get().setSlots(slots);
        repo.save(p.get());
        return Result.ok();
    }

    @Override public Result<Void> submitForApproval(CompanyRep owner, String postingId) {
        Optional<InternshipPosting> p = repo.findById(postingId);
        if (p.isEmpty()) return Result.fail("Posting not found");
        if (!p.get().getCompanyRepId().equals(owner.getId())) return Result.fail("Not your posting");
        if (p.get().getStatus() != PostingStatus.DRAFT) return Result.fail("Only DRAFT postings can be submitted");

        p.get().setStatus(PostingStatus.PENDING_APPROVAL);
        approvals.submit(new ApprovalItem(Type.POSTING_APPROVAL, p.get().getId(), owner.getId(), p.get().getTitle()));
        repo.save(p.get());
        return Result.ok();
    }

    @Override public List<InternshipPosting> listByCompany(String companyRepId) {
        return repo.findByCompany(companyRepId);
    }

    @Override public Result<Void> toggleVisibility(CompanyRep owner, String postingId) {
        Optional<InternshipPosting> p = repo.findById(postingId);
        if (p.isEmpty()) return Result.fail("Posting not found");
        if (!p.get().getCompanyRepId().equals(owner.getId())) return Result.fail("Not your posting");
        p.get().setVisibility(p.get().getVisibility() == Visibility.PUBLIC ? Visibility.HIDDEN : Visibility.PUBLIC);
        repo.save(p.get());
        return Result.ok();
    }
}