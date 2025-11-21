package boundary;

public final class HelpPage {
    public void show() {
        Console.title("Help");
        Console.say("- Use number keys to choose menu options.");
        Console.say("- Student: view/apply/accept/withdraw.");
        Console.say("- CompanyRep: create/submission/visibility/review.");
        Console.say("- Staff: approvals and reports.");
        Console.pause();
    }
}