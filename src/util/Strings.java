package util;

public final class Strings {
    private Strings(){}

    public static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    public static String nvl(String s, String alt) { return isBlank(s) ? alt : s; }
}