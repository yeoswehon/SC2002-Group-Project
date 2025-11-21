package boundary.loginOptions;

import boundary.Console;

/**
 * A page for future forget userID. We want this page to email the userId to the user.
 */
public final class ForgotUserIdPage {

    public void showForStudents() { retrieve("Student"); }
    public void showForReps()     { retrieve("CompanyRep"); }
    public void showForStaff()    { retrieve("Staff"); }

    /**
     * Future method for retrieving user ID
     */
    private void retrieve(String role) {
        Console.title("Forget User ID Page: " + role);
        String name = Console.ask("RetrieveUserID via name");
        Console.say("If an account exists for name '" + name + "', we will email the User ID.");
        Console.pause();
    }
}
