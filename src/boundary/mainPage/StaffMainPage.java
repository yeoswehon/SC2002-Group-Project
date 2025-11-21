package boundary.mainPage;

import boundary.Console;
import entities.approval.ApprovalItem;
import entities.approval.Type;
import entities.posting.InternshipPosting;
import entities.posting.Level;
import entities.posting.Major;
import entities.posting.PostingStatus;
import entities.users.CompanyRep;
import entities.users.Staff;
import kernel.AppContext;
import renderer.ApprovalRenderer;
import renderer.PostingRenderer;
import services.ReportingService;
import settings.SessionSettings;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static boundary.mainPage.PasswordChangePage.changePassword;
/**
 * Main page for staff
 */
public final class StaffMainPage {
    private final Staff me;
    /**
     * Constructor for StaffMainPage
     */
    public StaffMainPage(Staff me) {
        this.me = me;
    }
    /**
     * Show what a staff can do
     */
    public void show() {
        while (true) {
            Console.printBoxedText("Staff Main Page", 27);
            Console.title("What would you like to do?");
            Console.say("1. View Company Rep Registration");
            Console.say("2. Approve/Reject Company Rep Registration");
            Console.say("3. View Company Internship Listings");
            Console.say("4. Approve/Reject Company Internship Listings");
            Console.say("5. View Student Withdrawal Request");
            Console.say("6. Approve/Reject Student Withdrawal Request");
            Console.say("7. Generate Comprehensive Report");
            Console.say("8. Change Password");
            Console.say("9. Change posting filter");
            Console.say("10. Exit");

            int choice = Console.readMenuChoice(1, 10);
            switch (choice) {
                case 1 -> viewApprovalsOf(Type.COMPANY_REP_REG);
                case 2 -> actOnApproval(Type.COMPANY_REP_REG);
                case 3 -> viewCompanyPostings();
                case 4 -> actOnApproval(Type.POSTING_APPROVAL);
                case 5 -> viewApprovalsOf(Type.WITHDRAWAL);
                case 6 -> actOnApproval(Type.WITHDRAWAL);
                case 7 -> generateComprehensiveReport();
                case 8 -> changePassword(this.me);
                case 9 -> changeFilterMenu();
                case 10 -> { return; }
            }
        }
    }
    /**
     * List all postings created by all company representatives
     */
    private void viewCompanyPostings() {
        List<InternshipPosting> approved = AppContext.services().reports().listApprovedPostings();
        if (approved.isEmpty()) { Console.say("No postings."); Console.pause(); return; }

        var filter = SessionSettings.getFilter(me.getId());
        LocalDate today = LocalDate.now();

        Stream<InternshipPosting> stream = approved.stream()
                .filter(p -> !p.getOpenDate().isAfter(today) && !p.getCloseDate().isBefore(today));

        if (filter.companyOpt().isPresent()) {
            String needle = filter.companyOpt().get().toLowerCase(Locale.ROOT);
            stream = stream.filter(p -> AppContext.services().userLookup()
                    .findRepById(p.getCompanyRepId())
                    .map(rep -> {
                        String company = rep.getCompanyName() != null ? rep.getCompanyName() : rep.getDisplayName();
                        company = company == null ? "" : company;
                        return company.toLowerCase(Locale.ROOT).contains(needle);
                    })
                    .orElse(false));
        }

        if (filter.levelOpt().isPresent())   stream = stream.filter(p -> p.getLevel() == filter.levelOpt().get());
        if (filter.closeByOpt().isPresent()) stream = stream.filter(p -> !p.getCloseDate().isAfter(filter.closeByOpt().get()));
        if (filter.majorOpt().isPresent())   stream = stream.filter(p -> p.getMajor() == filter.majorOpt().get());
        if (filter.statusOpt().isPresent())  stream = stream.filter(p -> p.getStatus() == filter.statusOpt().get());

        List<InternshipPosting> list = stream
                .sorted(Comparator.comparing(InternshipPosting::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        if (list.isEmpty()) {
            Console.say("No postings match your filters.");
            Console.pause();
            return;
        }

        Console.title("Company Internship Listings (Filtered → Sorted by Title)");
        for (InternshipPosting p : list) {
            CompanyRep rep = AppContext.services().userLookup().findRepById(p.getCompanyRepId()).get();
            Console.say(PostingRenderer.renderBox(p, rep));
        }
        Console.pause();
    }
    /**
     * Show filter settings menu
     */
    private void changeFilterMenu() {
        var current = SessionSettings.getFilter(me.getId());
        var filter = new SessionSettings.PostingFilter();
        filter.companyNameContains = current.companyNameContains;
        filter.level = current.level;
        filter.closeBy = current.closeBy;
        filter.major = current.major;
        filter.status = current.status;

        while (true) {
            Console.title("Change Posting Filter (Staff)");
            showStaffFilterSummary(filter);
            Console.say("1) Company name");
            Console.say("2) Level");
            Console.say("3) Closing date (≤)");
            Console.say("4) Major");
            Console.say("5) Status");
            Console.say("6) Clear all");
            Console.say("7) Back (save)");
            int choice = Console.readMenuChoice(1, 7);

            switch (choice) {
                case 1 -> {
                    String company = Console.ask("Company name contains [blank=ANY]").trim();
                    filter.companyNameContains = company.isEmpty() ? null : company;
                }
                case 2 -> {
                    String lv = Console.ask("Level (BASIC/INTERMEDIATE/ADVANCED) [blank=ANY]").trim();
                    if (lv.isEmpty()) filter.level = null;
                    else {
                        try { filter.level = Level.valueOf(lv.toUpperCase()); }
                        catch (Exception e) { Console.say("Invalid; unchanged."); }
                    }
                }
                case 3 -> {
                    String closeBy = Console.ask("Closing date ≤ (yyyy-mm-dd) [blank=ANY]").trim();
                    if (closeBy.isEmpty()) filter.closeBy = null;
                    else {
                        try { filter.closeBy = LocalDate.parse(closeBy); }
                        catch (Exception e) { Console.say("Invalid date; unchanged."); }
                    }
                }
                case 4 -> {
                    String maj = Console.ask("Major (CS/CE/DSAI/BCG/MACS) [blank=ANY]").trim();
                    if (maj.isEmpty()) filter.major = null;
                    else {
                        try { filter.major = Major.valueOf(maj.toUpperCase()); }
                        catch (Exception e) { Console.say("Invalid; unchanged."); }
                    }
                }
                case 5 -> {
                    String st = Console.ask("Status (DRAFT/PENDING/APPROVED/REJECTED) [blank=ANY]").trim();
                    if (st.isEmpty()) filter.status = null;
                    else {
                        try { filter.status = PostingStatus.valueOf(st.toUpperCase()); }
                        catch (Exception e) { Console.say("Invalid; unchanged."); }
                    }
                }
                case 6 -> {
                    filter.companyNameContains = null;
                    filter.level = null;
                    filter.closeBy = null;
                    filter.major = null;
                    filter.status = null;
                    Console.say("Filters cleared.");
                }
                case 7 -> {
                    SessionSettings.setFilter(me.getId(), filter);
                    Console.say("Saved."); Console.pause();
                    return;
                }
            }
        }
    }
    /**
     * Display the current filter settings
     */
    private void showStaffFilterSummary(SessionSettings.PostingFilter filter) {
        Console.say("— Current —");
        Console.say("Company: " + (filter.companyNameContains == null ? "[ANY]" : filter.companyNameContains));
        Console.say("Level  : " + (filter.level == null ? "[ANY]" : filter.level.name()));
        Console.say("Close ≤: " + (filter.closeBy == null ? "[ANY]" : filter.closeBy));
        Console.say("Major  : " + (filter.major == null ? "[ANY]" : filter.major.name()));
        Console.say("Status : " + (filter.status == null ? "[ANY]" : filter.status.name()));
    }
    /**
     * Search approval queue based on COMPANY_REP_REP, POSTING_APPROVAL and WITHDRAWAL
     */
    private void viewApprovalsOf(Type type) {
        var items = AppContext.services().approvals().listItems().stream()
                .filter(item -> item.getType() == type).toList();
        if (items.isEmpty()) { Console.say("No items."); Console.pause(); return; }
        Console.title("Items: " + type);
        for (ApprovalItem item : items) {
            switch (type) {
                case COMPANY_REP_REG -> {
                    var repOptional = AppContext.services().userLookup().findRepById(item.getRefId());
                    if (repOptional.isEmpty()) Console.say("!! Missing CompanyRep for ref=" + item.getRefId());
                    else Console.say(ApprovalRenderer.companyRepRegistration(item, repOptional.get()));
                }
                case POSTING_APPROVAL -> {
                    var postingOptional = AppContext.services().postingLookup().findPostingById(item.getRefId());
                    if (postingOptional.isEmpty()) Console.say("!! Missing InternshipPosting for ref=" + item.getRefId());
                    else {
                        var posting = postingOptional.get();
                        var repOptional = AppContext.services().userLookup().findRepById(posting.getCompanyRepId());
                        Console.say(ApprovalRenderer.postingApproval(item, posting, repOptional.get()));
                    }
                }
                case WITHDRAWAL -> {
                    Console.say(item.getId());
                    Console.say(item.getRefId());
                }
            }
            Console.say("");
        }
        Console.pause();
    }
    /**
     * Approve or Reject the Approval Request
     */
    private void actOnApproval(Type type) {
        viewApprovalsOf(type);
        String id = Console.ask("Enter Item ID");
        boolean approve = Console.ask("Approve? (y/n)").trim().equalsIgnoreCase("y");
        var result = approve ? AppContext.services().approvals().approve(id)
                             : AppContext.services().approvals().reject(id, Console.ask("Reason"));
        Console.say(result.isOk() ? (approve ? "Approved." : "Rejected.") : "Failed: " + result.error());
        Console.pause();
    }
    /**
     * Get internship posting count based on filter settings
     */
    private void generateComprehensiveReport() {
        var svc = AppContext.services();
        var reports = svc.reports();
        var filter = SessionSettings.getFilter(me.getId());
        var today  = LocalDate.now();

        Console.title("Comprehensive Report (Filtered)");

        List<InternshipPosting> filteredPostings = reports.listApprovedPostings().stream()
                .filter(p -> !p.getOpenDate().isAfter(today) && !p.getCloseDate().isBefore(today))
                .filter(p -> matchesStaffFilter(p, filter))
                .sorted(Comparator.comparing(InternshipPosting::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        Set<String> filteredPostingIds = new HashSet<>();
        for (InternshipPosting p : filteredPostings) filteredPostingIds.add(p.getId());

        Console.say("Approved Postings:");
        if (filteredPostings.isEmpty()) {
            Console.say("- (none)");
        } else {
            for (InternshipPosting p : filteredPostings) {
                Console.say("- " + p.getId() + " " + p.getTitle());
            }
        }
        Console.say("");

        Console.say("Successful Applications:");
        var successful = reports.listSuccessfulApplications().stream()
                .filter(a -> filteredPostingIds.contains(a.getPostingId()))
                .toList();
        if (successful.isEmpty()) {
            Console.say("- (none)");
        } else {
            successful.forEach(a -> Console.say("- " + a.getId() + " -> " + a.getPostingId()));
        }
        Console.say("");

        Console.say("Applications by Major (filtered):");
        Map<Major, Long> byMajor = countApplicationsByMajorFor(filteredPostingIds);
        if (byMajor.isEmpty()) {
            Console.say("- (none)");
        } else {
            byMajor.forEach((m, c) -> Console.say("- " + m + ": " + c));
        }

        Console.pause();
    }
    /**
     * Check if posting matches the staff filter
     */
    private boolean matchesStaffFilter(InternshipPosting posting, SessionSettings.PostingFilter filter) {
        if (filter.companyNameContains != null && !filter.companyNameContains.isBlank()) {
            var repOptional = AppContext.services().userLookup().findRepById(posting.getCompanyRepId());
            String company = repOptional.map(CompanyRep::getCompanyName).orElse("");
            if (!company.toLowerCase(Locale.ROOT).contains(filter.companyNameContains.toLowerCase(Locale.ROOT)))
                return false;
        }
        if (filter.level != null && posting.getLevel() != filter.level) return false;
        if (filter.closeBy != null && posting.getCloseDate().isAfter(filter.closeBy)) return false;
        if (filter.major != null && posting.getMajor() != filter.major) return false;
        if (filter.status != null && posting.getStatus() != filter.status) return false;

        return true;
    }
    /**
     * Count applications based on major
     */
    private Map<Major, Long> countApplicationsByMajorFor(Set<String> postingIds) {
        var services = AppContext.services();
        Map<Major, Long> counts = new EnumMap<>(Major.class);

        for (String postingId : postingIds) {
            var applications = services.applications().listForPosting(postingId);
            applications.forEach(app ->
                services.userLookup().findStudentById(app.getStudentId())
                        .ifPresent(student -> counts.merge(student.getMajor(), 1L, Long::sum))
            );
        }
        return counts;
    }
}
