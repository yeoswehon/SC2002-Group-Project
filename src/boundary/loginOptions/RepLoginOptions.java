package boundary.loginOptions;

import boundary.Console;
import boundary.mainPage.RepMainPage;
import entities.users.CompanyRep;
import kernel.AppContext;

import java.util.Optional;

/**
 * Login page for company representative
 */
public final class RepLoginOptions {
    /**
     * Show company representative login options
     */
    public void show() {
        Console.printBoxedText("Company Rep Login Page",27);
        while (true) {
            Console.title("Please select an option:");
            Console.say("1. Login");
            Console.say("2. Register New Account");
            Console.say("3. Forget User ID Page");
            Console.say("4. Back");

            int choice = Console.readMenuChoice(1, 4);
            switch (choice) {
                case 1 -> doLogin();
                case 2 -> doRegister();
                case 3 -> new ForgotUserIdPage().showForReps();
                case 4 -> { return; }
                default -> Console.say("Invalid selection.");
            }
        }
    }
    /**
     * Login as a company representative
     */
    private void doLogin() {
        String username = Console.ask("Email address");
        String password = Console.ask("Password");

        Optional<CompanyRep> rep = AppContext.services().auth().loginRep(username, password);
        if (rep.isEmpty()) {
            Console.say("Login failed. Check credentials or approval status.");
            Console.pause();
            return;
        }
        Console.say("Welcome, " + rep.get().getDisplayName() + "!");
        new RepMainPage(rep.get()).show();
    }
    /**
     * Reregistration page for company representative
     */
    private void doRegister() {
        Console.printBoxedText("Registration Page", 27);
        Console.title("Please fill up the registration form below");

        Console.title("Registration Form: Company Representative Background Information");
        String displayName = Console.ask("Enter your name");
        String companyName = Console.ask("Enter your company name");

        Console.title("Registration Form: Login Credentials");
        String username = Console.ask("Enter your company email address");
        String password1 = Console.ask("Enter your password");
        String password2 = Console.ask("Re-enter your password");


        var r = AppContext.services().registration()
                .registerCompanyRep(username, password1, password2, displayName, companyName);

        if (r.isOk()) {
            Console.title("Registration submitted for staff approval.");
            Console.say("Your account cannot login until approved by staff.");
        } else {
            Console.say("Registration failed: " + r.error());
        }
        Console.pause();
    }
}
