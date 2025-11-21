package boundary.mainPage;

import boundary.Console;
import entities.users.CompanyRep;
import entities.users.Student;
import entities.posting.InternshipPosting;
import entities.posting.Level;
import entities.application.Application;
import kernel.AppContext;
import renderer.ApplicationRenderer;
import renderer.PostingRenderer;
import settings.SessionSettings;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static boundary.mainPage.PasswordChangePage.changePassword;


public final class StudentMainPage {
    private final Student me;
    /**
     * Constructor for StudentMainPage
     */
    public StudentMainPage(Student me) {
        this.me = me;
    }
    /**
     * Show what a student can do
     */
    public void show() {
        while (true) {
            Console.printBoxedText("Student Main Page",27);
            Console.title("What would you like to do?");
            Console.say("1. View internships");
            Console.say("2. Change posting filter");
            Console.say("3. Apply for internship");
            Console.say("4. View applications");
            Console.say("5. Accept placement");
            Console.say("6. Request withdrawal");
            Console.say("7. Change Password");
            Console.say("8. Exit");

            int choice = Console.readMenuChoice(1, 8);
            switch (choice) {
                case 1 -> viewInternships();
                case 2 -> changeFilterMenu();
                case 3 -> applyForInternship();
                case 4 -> viewApplications();
                case 5 -> acceptPlacement();
                case 6 -> requestWithdrawal();
                case 7 -> changePassword(this.me);
                case 8 -> { return; }
                default -> Console.say("Invalid selection.");
            }
        }
    }
    /**
     * View available internships
     */
    private void viewInternships() {
        List<InternshipPosting> postings =
                AppContext.services().postings().listVisibleForStudent(me);

        if (postings.isEmpty()) {
            Console.say("No internships available for your criteria right now.");
            Console.pause();
            return;
        }

        var f = SessionSettings.getFilter(me.getId());
        boolean senior = me.getYear() >= 3;
        LocalDate today = LocalDate.now();

        Stream<InternshipPosting> stream = postings.stream()
                .filter(p -> !p.getOpenDate().isAfter(today) && !p.getCloseDate().isBefore(today))
                .filter(p -> senior || p.getLevel() == Level.BASIC);

        if (f.companyOpt().isPresent()) {
            String needle = f.companyOpt().get().toLowerCase(Locale.ROOT);
            stream = stream.filter(p -> AppContext.services().userLookup()
                    .findRepById(p.getCompanyRepId())
                    .map(rep -> rep.getDisplayName() != null
                            && rep.getDisplayName().toLowerCase(Locale.ROOT).contains(needle))
                    .orElse(false));
        }

        if (senior && f.levelOpt().isPresent()) {
            var lvl = f.levelOpt().get();
            stream = stream.filter(p -> p.getLevel() == lvl);
        }

        if (f.closeByOpt().isPresent()) {
            LocalDate by = f.closeByOpt().get();
            stream = stream.filter(p -> !p.getCloseDate().isAfter(by));
        }

        List<InternshipPosting> list = stream
                .sorted(Comparator.comparing(InternshipPosting::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        if (list.isEmpty()) {
            Console.say("No internships match your filters.");
            Console.pause();
            return;
        }

        Console.title("Eligible Internship Listings");
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
        var f = new SessionSettings.PostingFilter();
        f.companyNameContains = current.companyNameContains;
        f.level = current.level;
        f.closeBy = current.closeBy;

        while (true) {
            Console.title("Change Posting Filter (Student)");
            showStudentFilterSummary(f);

            Console.say("1) Company name");
            Console.say("2) Level");
            Console.say("3) Closing date (≤)");
            Console.say("4) Clear all");
            Console.say("5) Back (save)");
            int c = Console.readMenuChoice(1,5);

            switch (c) {
                case 1 -> {
                    String company = Console.ask("Company name contains [blank=ANY]").trim();
                    f.companyNameContains = company.isEmpty() ? null : company;
                }
                case 2 -> {
                    if (me.getYear() <= 2) {
                        Console.say("You are Y" + me.getYear() + " → BASIC only.");
                        f.level = Level.BASIC; // fixed
                    } else {
                        String lv = Console.ask("Level (BASIC/INTERMEDIATE/ADVANCED) [blank=ANY]").trim();
                        if (lv.isEmpty()) f.level = null;
                        else {
                            try { f.level = Level.valueOf(lv.toUpperCase()); }
                            catch (Exception e) { Console.say("Invalid; unchanged."); }
                        }
                    }
                }
                case 3 -> {
                    String closeBy = Console.ask("Closing date ≤ (yyyy-mm-dd) [blank=ANY]").trim();
                    if (closeBy.isEmpty()) f.closeBy = null;
                    else {
                        try { f.closeBy = LocalDate.parse(closeBy); }
                        catch (Exception e) { Console.say("Invalid date; unchanged."); }
                    }
                }
                case 4 -> {
                    f.companyNameContains = null;
                    f.level = (me.getYear() <= 2 ? Level.BASIC : null);
                    f.closeBy = null;
                    Console.say("Filters cleared.");
                }
                case 5 -> {
                    SessionSettings.setFilter(me.getId(), f);
                    Console.say("Saved."); Console.pause();
                    return;
                }
            }
        }
    }
    /**
     * Display the current filter settings
     */
    private void showStudentFilterSummary(SessionSettings.PostingFilter f) {
        Console.say("— Current —");
        Console.say("Company: " + (f.companyNameContains == null ? "[ANY]" : f.companyNameContains));
        String lvl = (me.getYear() <= 2 ? "BASIC (fixed for Y1/2)"
                : (f.level == null ? "[ANY]" : f.level.name()));
        Console.say("Level  : " + lvl);
        Console.say("Close ≤: " + (f.closeBy == null ? "[ANY]" : f.closeBy));
    }
    /**
     * Apply to an internship posting
     */
    private void applyForInternship() {
        String postingId = Console.ask("Enter Posting ID");
        var result = AppContext.services().applications().apply(me, postingId);
        if (result.isOk()) {
            String appId = result.get().map(entities.application.Application::getId).orElse("N/A");
            Console.say("Application submitted. ID: " + appId);
        } else {
            Console.say("Failed to apply: " + result.error());
        }
        Console.pause();
    }
    /**
     * Show submitted applications
     */
    private void viewApplications() {
        List<Application> list =
                AppContext.services().applications().listForStudent(me.getId());

        if (list.isEmpty()) {
            Console.say("You have no applications yet.");
            Console.pause();
            return;
        }

        Console.title("My Applications");
        for (Application a : list) {
            InternshipPosting p = AppContext.services().postingLookup().findPostingById(a.getPostingId()).get();
            CompanyRep rep = AppContext.services().userLookup().findRepById(p.getCompanyRepId()).get();
            Console.say(ApplicationRenderer.studentRenderBox(a, p, rep));
        }
        Console.pause();
    }
    /**
     * Accept an internship posting offer
     */
    private void acceptPlacement() {
        String appId = Console.ask("Enter Application ID to accept");
        var r = AppContext.services().applications().acceptOffer(me.getId(), appId);
        Console.say(r.isOk()
                ? "Offer accepted. Other active applications withdrawn."
                : "Unable to accept offer: " + r.error());
        Console.pause();
    }
    /**
     * Request withdrawal from internship posting
     */
    private void requestWithdrawal() {
        String appId = Console.ask("Enter Application ID to withdraw");
        String reason = Console.ask("Reason");
        var r = AppContext.services().applications().requestWithdrawal(me.getId(), appId, reason);
        Console.say(r.isOk()
                ? "Withdrawal requested. Awaiting staff approval."
                : "Unable to request withdrawal: " + r.error());
        Console.pause();
    }
}
