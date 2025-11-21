package boundary.welcomePage;

import boundary.Console;
import boundary.loginOptions.RepLoginOptions;
import boundary.loginOptions.StaffLoginOptions;
import boundary.loginOptions.StudentLoginOptions;

/**
 * welcome page for student, staff and company representative
 */
public final class WelcomePage {
    /**
     * Domain selection page
     */
    public void show() {
        printIPMSLogo();
        Console.printBoxedText("Welcome to IPMS", 27);
        while (true) {
            Console.printBoxedText("Domain Selection Page", 27);
            Console.title("Please select your user domain:");
            Console.say("1. Student");
            Console.say("2. Staff");
            Console.say("3. CompanyRep");
            Console.say("4. Exit");

            int choice = Console.readMenuChoice(1, 4);
            switch (choice) {
                case 1 -> new StudentLoginOptions().show();
                case 2 -> new StaffLoginOptions().show();
                case 3 -> new RepLoginOptions().show();
                case 4 -> {
                    Console.say("Goodbye!");
                    return;
                }
                default -> Console.say("Invalid selection.");
            }
        }
    }
    /**
     * Method to print IPMS logo
     */
    private static void printIPMSLogo() {
        String ipmsLogo = """
                ██ ██████  ███    ███ ███████\s
                ██ ██   ██ ████  ████ ██     \s
                ██ ██████  ██ ████ ██ ███████\s
                ██ ██      ██  ██  ██      ██\s
                ██ ██      ██      ██ ███████\s
                """;
        System.out.printf(ipmsLogo);
    }
}