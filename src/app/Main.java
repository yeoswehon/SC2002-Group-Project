package app;

import boundary.welcomePage.WelcomePage;
import kernel.Startup;

/**
 * Entry point of application.
 */
public final class Main {
    public static void main(String[] args) {
        Startup.init();
        new WelcomePage().show();
    }
}