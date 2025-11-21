package boundary.loginOptions;

import boundary.Console;
import boundary.mainPage.StaffMainPage;
import entities.users.Staff;
import kernel.AppContext;

import java.util.Optional;
/**
 * Login page for staff
 */
public final class StaffLoginOptions {
    /**
     * Show staff login options
     */
    public void show() {
        Console.printBoxedText("Staff Login Page",27);
        while (true) {
            Console.title("Please select an option:");
            Console.say("1. Login");
            Console.say("2. Forget User ID Page");
            Console.say("3. Back");

            int choice = Console.readMenuChoice(1, 3);
            switch (choice) {
                case 1 -> doLogin();
                case 2 -> new ForgotUserIdPage().showForStaff();
                case 3 -> { return; }
                default -> Console.say("Invalid selection.");
            }
        }
    }
    /**
     * Login as a staff
     */
    private void doLogin() {
        String username = Console.ask("User ID");
        String password = Console.ask("Password");

        Optional<Staff> staff = AppContext.services().auth().loginStaff(username, password);
        if (staff.isEmpty()) {
            Console.say("Login failed. Please check your credentials.");
            Console.pause();
            return;
        }
        Console.say("Welcome, " + staff.get().getDisplayName() + "!");
        new StaffMainPage(staff.get()).show();
    }
}
