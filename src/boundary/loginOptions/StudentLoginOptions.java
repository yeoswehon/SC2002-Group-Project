package boundary.loginOptions;

import boundary.Console;
import boundary.mainPage.StudentMainPage;
import entities.users.Student;
import kernel.AppContext;

import java.util.Optional;
/**
 * Login page for student
 */
public final class StudentLoginOptions {
    /**
     * Show student login options
     */
    public void show() {
        Console.printBoxedText("Student Login Page",27);
        while (true) {
            Console.title("Please select an option:");
            Console.say("1. Login");
            Console.say("2. Forget User ID Page");
            Console.say("3. Back");

            int choice = Console.readMenuChoice(1, 3);
            switch (choice) {
                case 1 -> doLogin();
                case 2 -> new ForgotUserIdPage().showForStudents();
                case 3 -> { return; } // back to WelcomePage
                default -> Console.say("Invalid selection.");
            }
        }
    }
    /**
     * Login as a student
     */
    private void doLogin() {
        String username = Console.ask("User ID");
        String password = Console.ask("Password");

        Optional<Student> result = AppContext.services().auth().loginStudent(username, password);
        if (result.isEmpty()) {
            Console.say("Login failed. Please check your credentials.");
            Console.pause();
            return;
        }

        Student me = result.get();
        Console.say("Welcome, " + me.getDisplayName() + "!");
        new StudentMainPage(me).show();
    }
}
