package util;

import java.time.LocalDate;
/**
 * Interface to get system time
 */
public interface Clock {
    LocalDate today();

    static Clock system() {
        return LocalDate::now;
    }
}