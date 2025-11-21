package boundary;
import java.util.Scanner;
/**
 * Helper class to print messages
 */
public final class Console {
    private static final Scanner input = new Scanner(System.in);
    /**
     * Print a new line
     */
    public static void title(String s){ System.out.println("\n" + s); }
    /**
     * Print a line
     */
    public static void say(String s){ System.out.println(s); }
    /**
     * Print and get input from user
     */
    public static String ask(String prompt){ System.out.print(prompt + ": "); return input.nextLine().trim(); }
    /**
     * Create short pause for user
     */
    public static void pause(){ System.out.println("Press Enter to continue..."); input.nextLine(); }
    /**
     * Method to read user choice
     */
    public static int readMenuChoice(int min, int max) {
        while (true) {
            String s = Console.ask("Choose");
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    Console.say("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                Console.say("Please enter a valid number.");
            }
        }
    }
    /**
     * Method to print text in a box
     */
    public static void printBoxedText(String text, int boxInnerWidth) {
        String top = "╔" + "═".repeat(boxInnerWidth) + "╗";
        String mid = "║" + centerText(text, boxInnerWidth) + "║";
        String bot = "╚" + "═".repeat(boxInnerWidth) + "╝";

        System.out.println("\n"+top);
        System.out.println(mid);
        System.out.printf(bot);
    }
    /**
     * Center a text in a box
     */
    private static String centerText(String text, int width) {
        int totalPadding = Math.max(0, width - text.length());
        int left = totalPadding / 2;
        int right = totalPadding - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }
}