package services.impl;

import entities.application.Application;
import entities.application.ApplicationStatus;
import entities.approval.ApprovalItem;
import entities.approval.Type;
import entities.posting.InternshipPosting;
import entities.posting.PostingStatus;
import entities.users.CompanyRep;
import kernel.Result;
import repositories.ApprovalQueue;
import repositories.ApplicationRepository;
import repositories.PostingRepository;
import repositories.UserRepository;
import services.ApprovalService;
import util.Clock;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Concrete implementation of Approval Service
 */
public final class ApprovalServiceImpl implements ApprovalService {
    private final ApprovalQueue approvals;
    private final UserRepository users;
    private final PostingRepository postings;
    private final ApplicationRepository applications;
    private final Clock clock;

    public ApprovalServiceImpl(ApprovalQueue approvals,
                               UserRepository users,
                               PostingRepository postings,
                               ApplicationRepository applications,
                               Clock clock) {
        this.approvals = approvals;
        this.users = users;
        this.postings = postings;
        this.applications = applications;
        this.clock = clock;
    }

    @Override
    public List<ApprovalItem> listItems() {
        return List.copyOf(approvals.list());
    }

    @Override
    public Result<Void> approve(String itemId) {
        Optional<ApprovalItem> itemOpt = approvals.find(itemId);
        if (itemOpt.isEmpty()) return Result.fail("Request not found");

        ApprovalItem item = itemOpt.get();

        switch (item.getType()) {
            case COMPANY_REP_REG -> users.findById(item.getRefId())
                    .filter(u -> u instanceof CompanyRep)
                    .map(u -> (CompanyRep) u)
                    .ifPresent(rep -> {
                        rep.setApproved(true);
                        users.save(rep);
                    });

            case POSTING_APPROVAL -> postings.findById(item.getRefId())
                    .ifPresent(p -> {
                        p.setStatus(PostingStatus.APPROVED);
                        postings.save(p);
                    });

            case WITHDRAWAL -> applications.findById(item.getRefId())
                    .ifPresent(a -> {
                        a.setStatus(ApplicationStatus.WITHDRAWN, clock.today());
                        applications.save(a);
                    });
        }

        approvals.remove(item.getId());
        return Result.ok();
    }

    @Override
    public Result<Void> reject(String itemId, String reason) {
        Optional<ApprovalItem> itemOpt = approvals.find(itemId);
        if (itemOpt.isEmpty()) return Result.fail("Request not found");

        ApprovalItem item = itemOpt.get();

        switch (item.getType()) {
            case COMPANY_REP_REG -> users.findById(item.getRefId())
                    .ifPresent(u -> u.deactivate());

            case POSTING_APPROVAL -> postings.findById(item.getRefId())
                    .ifPresent(p -> {
                        p.setStatus(PostingStatus.REJECTED);
                        postings.save(p);
                    });

            case WITHDRAWAL -> {
                
            }
        }

        approvals.remove(item.getId());
        return Result.ok();
    }
}