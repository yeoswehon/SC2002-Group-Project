package renderer;

import java.util.Arrays;
/**
 * Class to help print boxes nicely
 */
public final class RenderUtil {

    private static final int LABEL_WIDTH = 25;
    /**
     * Empty constructor to prevent initialisation
     */
    private RenderUtil() {}
    /**
     * Print a box with the given lines
     */
    public static String boxRender(String[] lines) {
        int maxLen = Arrays.stream(lines)
                .mapToInt(String::length)
                .max()
                .orElse(0);

        String border = "+" + "-".repeat(maxLen + 2) + "+";

        StringBuilder sb = new StringBuilder();
        sb.append(border).append(System.lineSeparator());

        for (String line : lines) {
            sb.append("| ")
                    .append(line);

            int padding = maxLen - line.length();
            if (padding > 0) {
                sb.append(" ".repeat(padding));
            }

            sb.append(" |").append(System.lineSeparator());
        }

        sb.append(border);
        return sb.toString();
    }
    /**
     * Format a string
     */
    public static String fmt(String key, Object value) {
        return String.format("%-" + LABEL_WIDTH + "s: %s", key, value);
    }
}
