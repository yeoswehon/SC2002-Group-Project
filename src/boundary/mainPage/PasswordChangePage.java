package boundary.mainPage;

import boundary.Console;
import entities.users.User;
import kernel.AppContext;

/**
 * Password Change Page, used by student, staff and company representative
 */
public class PasswordChangePage {
    /**
     * Method to ask user for old password and new password twice
     */
    public static void changePassword(User user) {
        String oldPw = Console.ask("Please enter your old password: ");
        String newPw1 = Console.ask("Please enter your new password: ");
        String newPw2 = Console.ask("Please re-enter your new password: ");

        var r = AppContext.services().auth().changePassword(user.getId(), oldPw, newPw1, newPw2);
        Console.say(r.isOk() ? "Password updated." : ("Failed to change password: " + r.error()));
        Console.pause();
    }
}
