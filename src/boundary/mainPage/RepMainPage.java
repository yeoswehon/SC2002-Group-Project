package boundary.mainPage;

import boundary.Console;
import entities.application.Application;
import entities.posting.InternshipPosting;
import entities.posting.Level;
import entities.posting.Major;
import entities.posting.PostingStatus;
import entities.users.CompanyRep;
import kernel.AppContext;
import kernel.Result;
import renderer.ApplicationRenderer;
import renderer.PostingRenderer;
import settings.SessionSettings;

import java.time.LocalDate;
import java.util.List;

/**
 * Main page for company representative
 */
public final class RepMainPage {
    private final CompanyRep me;
    /**
     * Constructor for RepMainPage
     */
    public RepMainPage(CompanyRep me) { this.me = me; }
    /**
     * Show what a representative can do
     */
    public void show() {
        while (true) {
            Console.printBoxedText("Company Rep Main Page", 27);
            Console.title("What would you like to do?");
            Console.say("1. Create posting");
            Console.say("2. Edit posting (by ID; only DRAFT)");
            Console.say("3. Submit posting for approval");
            Console.say("4. List my postings");
            Console.say("5. Review applicants");
            Console.say("6. Toggle posting visibility");
            Console.say("7. Change posting filter");
            Console.say("8. Change Password");
            Console.say("9. Back");

            int choice = Console.readMenuChoice(1, 9);
            switch (choice) {
                case 1 -> createPosting();
                case 2 -> editPostingById();
                case 3 -> submitForApproval();
                case 4 -> listMyPostings();
                case 5 -> reviewApplicants();
                case 6 -> toggleVisibility();
                case 7 -> changeFilterMenu();
                case 8 -> PasswordChangePage.changePassword(this.me);
                case 9 -> { return; }
                default -> Console.say("Invalid selection.");
            }
        }
    }
    /**
     * Create posting form
     */
    private void createPosting() {
        String title = Console.ask("Title");
        String desc  = Console.ask("Description");
        Level lvl    = Level.valueOf(Console.ask("Level (BASIC/INTERMEDIATE/ADVANCED)").toUpperCase());
        Major maj    = Major.valueOf(Console.ask("Major (CS/CE/DSAI/BCG/MACS)").toUpperCase());
        LocalDate open  = LocalDate.parse(Console.ask("Open date (YYYY-MM-DD)"));
        LocalDate close = LocalDate.parse(Console.ask("Close date (YYYY-MM-DD)"));
        int slots = Integer.parseInt(Console.ask("Slots (1-10)"));

        Result<InternshipPosting> r = AppContext.services().postings()
                .createDraft(me, title, desc, lvl, maj, open, close, slots);
        Console.say(r.isOk() ? "Draft created. ID: " + r.get().get().getId() : "Failed: " + r.error());
        Console.pause();
    }

    /**
     * Edit DRAFT Posting
     */
    private void editPostingById() {
        String postingId = Console.ask("Enter Posting ID to edit (DRAFT only)");
        var postingOpt = AppContext.services().postingLookup().findPostingById(postingId);
        if (postingOpt.isEmpty()) {
            Console.say("No such posting.");
            Console.pause();
            return;
        }
        InternshipPosting p = postingOpt.get();

        if (!p.getCompanyRepId().equals(me.getId())) {
            Console.say("You can only edit postings you own.");
            Console.pause();
            return;
        }
        if (p.getStatus() != PostingStatus.DRAFT) {
            Console.say("Only DRAFT postings can be edited. Current status: " + p.getStatus());
            Console.pause();
            return;
        }
        // Show current values and prompt with defaults
        Console.title("Editing DRAFT posting " + p.getId());
        String titleIn = Console.ask("Title [" + p.getTitle() + "]");
        String descIn  = Console.ask("Description [" + p.getDescription() + "]");
        String lvlIn   = Console.ask("Level (BASIC/INTERMEDIATE/ADVANCED) [" + p.getLevel() + "]");
        String majIn   = Console.ask("Major (CS/CE/DSAI/BCG/MACS) [" + p.getMajor() + "]");
        String openIn  = Console.ask("Open date (YYYY-MM-DD) [" + p.getOpenDate() + "]");
        String closeIn = Console.ask("Close date (YYYY-MM-DD) [" + p.getCloseDate() + "]");
        String slotsIn = Console.ask("Slots (1-10) [" + p.getSlots() + "]");

        String title = titleIn.isBlank() ? p.getTitle() : titleIn;
        String desc  = descIn.isBlank()  ? p.getDescription() : descIn;

        Level level = p.getLevel();
        if (!lvlIn.isBlank()) {
            try { level = Level.valueOf(lvlIn.trim().toUpperCase()); } catch (Exception e) {
                Console.say("Invalid level; keeping " + level);
            }
        }

        Major major = p.getMajor();
        if (!majIn.isBlank()) {
            try { major = Major.valueOf(majIn.trim().toUpperCase()); } catch (Exception e) {
                Console.say("Invalid major; keeping " + major);
            }
        }

        LocalDate open = p.getOpenDate();
        if (!openIn.isBlank()) {
            try { open = LocalDate.parse(openIn.trim()); } catch (Exception e) {
                Console.say("Invalid open date; keeping " + open);
            }
        }

        LocalDate close = p.getCloseDate();
        if (!closeIn.isBlank()) {
            try { close = LocalDate.parse(closeIn.trim()); } catch (Exception e) {
                Console.say("Invalid close date; keeping " + close);
            }
        }

        int slots = p.getSlots();
        if (!slotsIn.isBlank()) {
            try {
                int tmp = Integer.parseInt(slotsIn.trim());
                if (tmp >= 1 && tmp <= 10) slots = tmp;
                else Console.say("Slots must be 1..10; keeping " + slots);
            } catch (Exception e) {
                Console.say("Invalid slots; keeping " + slots);
            }
        }

        var result = AppContext.services().postings()
                .updateDraft(me, p.getId(), title, desc, level, major, open, close, slots);

        Console.say(result.isOk() ? "Posting updated." : "Failed: " + result.error());

        if (result.isOk() && Console.ask("Submit for staff approval now? (y/n)").trim().equalsIgnoreCase("y")) {
            var sr = AppContext.services().postings().submitForApproval(me, p.getId());
            Console.say(sr.isOk() ? "Submitted for approval." : "Submit failed: " + sr.error());
        }
        Console.pause();
    }
    /**
     * Submit posting for approval
     */
    private void submitForApproval() {
        String pid = Console.ask("Posting ID");
        var r = AppContext.services().postings().submitForApproval(me, pid);
        Console.say(r.isOk() ? "Submitted for approval." : "Failed: " + r.error());
        Console.pause();
    }
    /**
     * List all postings created by the company representative
     */
    private void listMyPostings() {
        List<InternshipPosting> list = AppContext.services().postings().listByCompany(me.getId());
        if (list.isEmpty()) {
            Console.say("You have no postings.");
        } else {
            Console.title("My Postings");
            for (InternshipPosting p : list) {
                Console.say(PostingRenderer.renderBox(p, this.me));
            }
        }
        Console.pause();
    }
    /**
     * Allow company representative to approve/reject student applications
     */
    private void reviewApplicants() {
        String postingId = Console.ask("Enter Posting ID to review");
        List<Application> apps = AppContext.services().applications().listForPosting(postingId);
        if (apps.isEmpty()) {
            Console.say("No applications for this posting yet.");
            Console.pause();
            return;
        }
        Console.title("Applications for Posting " + postingId);
        for (Application a : apps) {
            Console.say(ApplicationRenderer.repRenderBox(a));
        }

        String appId = Console.ask("Enter Application ID to review");
        boolean approve = Console.ask("Approve? (y/n)").trim().equalsIgnoreCase("y");
        var r = AppContext.services().applications().reviewByCompany(me, appId, approve);
        Console.say(r.isOk() ? "Recorded." : "Failed: " + r.error());
        Console.pause();
    }
    /**
     * Toggle posting visibility
     */
    private void toggleVisibility() {
        String pid = Console.ask("Posting ID to toggle");
        var r = AppContext.services().postings().toggleVisibility(me, pid);
        Console.say(r.isOk() ? "Toggled." : "Failed: " + r.error());
        Console.pause();
    }
    /**
     * Show filter settings menu
     */
    private void changeFilterMenu() {
        var cur = SessionSettings.getFilter(me.getId());
        var f = new SessionSettings.PostingFilter();
        f.companyNameContains = cur.companyNameContains;
        f.level = cur.level;
        f.closeBy = cur.closeBy;
        f.major = cur.major;
        f.status = cur.status;

        while (true) {
            Console.title("Change Posting Filter (Company Rep)");
            showRepFilterSummary(f);
            Console.say("1) Company name");
            Console.say("2) Level");
            Console.say("3) Closing date (≤)");
            Console.say("4) Major");
            Console.say("5) Status");
            Console.say("6) Clear all");
            Console.say("7) Back (save)");
            int c = Console.readMenuChoice(1,7);

            switch (c) {
                case 1 -> {
                    String company = Console.ask("Company name contains [blank=ANY]").trim();
                    f.companyNameContains = company.isEmpty() ? null : company;
                }
                case 2 -> {
                    String lv = Console.ask("Level (BASIC/INTERMEDIATE/ADVANCED) [blank=ANY]").trim();
                    if (lv.isEmpty()) f.level = null;
                    else {
                        try { f.level = Level.valueOf(lv.toUpperCase()); }
                        catch (Exception e) { Console.say("Invalid; unchanged."); }
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
                    String maj = Console.ask("Major (CS/CE/DSAI/BCG/MACS) [blank=ANY]").trim();
                    if (maj.isEmpty()) f.major = null;
                    else {
                        try { f.major = Major.valueOf(maj.toUpperCase()); }
                        catch (Exception e) { Console.say("Invalid; unchanged."); }
                    }
                }
                case 5 -> {
                    String st = Console.ask("Status (DRAFT/PENDING/APPROVED/REJECTED) [blank=ANY]").trim();
                    if (st.isEmpty()) f.status = null;
                    else {
                        try { f.status = PostingStatus.valueOf(st.toUpperCase()); }
                        catch (Exception e) { Console.say("Invalid; unchanged."); }
                    }
                }
                case 6 -> {
                    f.companyNameContains = null;
                    f.level = null;
                    f.closeBy = null;
                    f.major = null;
                    f.status = null;
                    Console.say("Filters cleared.");
                }
                case 7 -> {
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
    private void showRepFilterSummary(SessionSettings.PostingFilter f) {
        Console.say("— Current —");
        Console.say("Company: " + (f.companyNameContains == null ? "[ANY]" : f.companyNameContains));
        Console.say("Level  : " + (f.level == null ? "[ANY]" : f.level.name()));
        Console.say("Close ≤: " + (f.closeBy == null ? "[ANY]" : f.closeBy));
        Console.say("Major  : " + (f.major == null ? "[ANY]" : f.major.name()));
        Console.say("Status : " + (f.status == null ? "[ANY]" : f.status.name()));
    }
}
