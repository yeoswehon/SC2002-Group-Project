package repositories.serializedRepositories;

import entities.users.*;
import entities.posting.Major;
import repositories.UserRepository;
import security.PasswordHasher;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Class for serialized Application Repository
 */
public final class SerializedUserRepository implements UserRepository {

    private static final String DEFAULT_PASSWORD = "password";

    private final Map<String, User> byId = new LinkedHashMap<>();
    private final Map<String, String> idByUsername = new HashMap<>();

    private final Path serFile;
    private final Path studentCsv;
    private final Path staffCsv;
    private final PasswordHasher hasher;
    /**
     * Constructor with path to serialized file and CSVs
     */
    public SerializedUserRepository(PasswordHasher hasher) {
        this(Paths.get("DataStorage/SerializedRepos/UserRepo.ser"),
             Paths.get("DataStorage/CSVFiles/StudentList.csv"),
             Paths.get("DataStorage/CSVFiles/StaffList.csv"),
             hasher);
    }
    /**
     * Constructor to general files
     */
    public SerializedUserRepository(Path serFile, Path studentCsv, Path staffCsv, PasswordHasher hasher) {
        this.serFile = serFile;
        this.studentCsv = studentCsv;
        this.staffCsv = staffCsv;
        this.hasher = hasher;
        loadOrBootstrap();
    }

    /**
     * Find user by ID
     */
    @Override public Optional<User> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }
    /**
     * Find user by username
     */
    @Override public Optional<User> findByUsername(String username) {
        String id = idByUsername.get(username);
        return id == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }
    /**
     * Save user to repository
     */
    @Override public void save(User user) {
        byId.put(user.getId(), user);
        idByUsername.put(user.getUsername(), user.getId());
        persist();
    }
    /**
     * Get all users
     */
    @Override public List<User> findAll() {
        return List.copyOf(byId.values());
    }

    /**
     * Load from either CSV or serialized file
     */
    private void loadOrBootstrap() {
        if (Files.exists(serFile)) {
            readSer().ifPresent(this::inflate);
        } else {
            bootstrapFromCsvs();
            persist(); 
        }
    }
    /**
     * Save repository
     */
    private void persist() {
        try {
            Files.createDirectories(serFile.getParent());
            try (var out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(serFile)))) {
                out.writeObject(snapshot());
            }
        } catch (IOException ignored) { }
    }
    /**
     * Read serialized file
     */
    private Optional<Snapshots> readSer() {
        try (var in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(serFile)))) {
            Object o = in.readObject();
            return (o instanceof Snapshots s) ? Optional.of(s) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    /**
     * Convert snapshot back to user
     */
    private void inflate(Snapshots snaps) {
        byId.clear(); idByUsername.clear();
        for (UserSnap s : snaps.users) {
            User u;
            switch (s.kind) {
                case "STUDENT" -> {
                    Major major = Major.valueOf(s.majorOrCompany);
                    u = new Student(s.id, s.username, s.passwordHash, s.displayName, major, s.year);
                }

                case "STAFF" -> {
                    u = new Staff(s.id, s.username, s.passwordHash, s.displayName);
                }

                case "COMPANY_REP" -> {
                    CompanyRep r = new CompanyRep(s.id, s.username, s.passwordHash, s.displayName, s.majorOrCompany);
                    r.setApproved(s.approved);
                    u = r;
                }
                default -> throw new IllegalStateException("Unknown user kind: " + s.kind);
            }
            byId.put(u.getId(), u);
            idByUsername.put(u.getUsername(), u.getId());
        }
    }
    /**
     * Snapshot class to save user attributes
     */
    private Snapshots snapshot() {
        List<UserSnap> all = new ArrayList<>();
        for (User u : byId.values()) {
            if (u instanceof Student s) {
                all.add(UserSnap.student(s.getId(), s.getUsername(), s.getPasswordHash(), s.getDisplayName(),
                        s.getMajor().name(), s.getYear()));
            } else if (u instanceof CompanyRep r) {
                all.add(UserSnap.rep(r.getId(), r.getUsername(), r.getPasswordHash(), r.getDisplayName(),
                        r.getCompanyName(), r.isApproved()));
            } else if (u instanceof Staff st) {
                all.add(UserSnap.staff(st.getId(), st.getUsername(), st.getPasswordHash(), st.getDisplayName()));
            }
        }
        return new Snapshots(all);
    }
    /**
     * Load from CSV
     */
    private void bootstrapFromCsvs() {
        byId.clear(); idByUsername.clear();

        for (Map<String,String> row : readCsv(studentCsv)) {
            String username = firstNonBlank(row.get("username"), row.get("StudentID"));
            if (isBlank(username)) continue;

            String displayName = firstNonBlank(row.get("displayName"), row.get("Name"));
            if (isBlank(displayName)) displayName = username;

            String majorStr = firstNonBlank(row.get("major"), row.get("Major"));
            Major major = parseMajor(majorStr);

            int year = parseInt(firstNonBlank(row.get("year"), row.get("Year")), 1);

            String rawPassword = fallbackPassword(row.get("password"));

            String id = UUID.randomUUID().toString();
            String hash = hasher.hash(rawPassword);

            Student st = new Student(username, hash, displayName, major, year);
            put(st);
        }

        for (Map<String,String> row : readCsv(staffCsv)) {
            String username = firstNonBlank(row.get("username"), row.get("StaffID"));
            if (isBlank(username)) continue;

            String displayName = firstNonBlank(row.get("displayName"), row.get("Name"));
            if (isBlank(displayName)) displayName = username;

            String rawPassword = fallbackPassword(row.get("password"));

            String hash = hasher.hash(rawPassword);

            Staff s = new Staff(username, hash, displayName);
            put(s);
        }
    }
    /**
     * Put user
     */
    private void put(User u) {
        byId.put(u.getId(), u);
        idByUsername.put(u.getUsername(), u.getId());
    }

    /**
     * Read user CSV file
     */
    private static List<Map<String,String>> readCsv(Path path) {
        if (path == null || !Files.exists(path)) return List.of();
        try {
            List<String> lines = Files.readAllLines(path);
            if (lines.isEmpty()) return List.of();
            String[] header = lines.get(0).split(",", -1);
            List<Map<String,String>> rows = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                String[] cells = line.split(",", -1);
                Map<String,String> m = new LinkedHashMap<>();
                for (int c = 0; c < header.length; c++) {
                    String k = header[c].trim();
                    String v = c < cells.length ? cells[c].trim() : "";
                    m.put(k, v);
                }
                rows.add(m);
            }
            return rows;
        } catch (IOException e) {
            return List.of();
        }
    }

    /**
     * Fallback Password
     */
    private static String fallbackPassword(String csvValue) {
        if (csvValue == null) return DEFAULT_PASSWORD;
        String t = csvValue.trim();
        return t.isEmpty() ? DEFAULT_PASSWORD : t;
    }
    /**
     * Check if string is blank
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    /**
     * Find first non blank in String
     */
    private static String firstNonBlank(String... vals) {
        for (String v : vals) {
            if (!isBlank(v)) return v.trim();
        }
        return "";
    }
    /**
     * Parse int
     */
    private static int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return fallback; }
    }
    /**
     * Parse major
     */
    private static Major parseMajor(String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Missing major in student CSV row");
        }

        String t = value.trim().toUpperCase(Locale.ROOT);

        try {
            return Major.valueOf(t);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown major in CSV: '" + value + "'", e);
        }
    }

    /**
     * Class to store all user snapshots
     */
    private static final class Snapshots implements Serializable {
        private static final long serialVersionUID = 1L;
        final List<UserSnap> users;
        Snapshots(List<UserSnap> users) { this.users = users; }
    }
    /**
     * Class to store individual user snapshots
     */
    private static final class UserSnap implements Serializable {
        private static final long serialVersionUID = 1L;
        final String kind;
        final String id, username, passwordHash, displayName;
        final String majorOrCompany; 
        final int year;       
        final boolean approved;   

        private UserSnap(String kind, String id, String username, String passwordHash, String displayName,
                         String majorOrCompany, int year, boolean approved) {
            this.kind = kind; this.id=id; this.username=username; this.passwordHash=passwordHash;
            this.displayName=displayName; this.majorOrCompany=majorOrCompany; this.year=year; this.approved=approved;
        }
        static UserSnap student(String id, String username, String hash, String display, String major, int year) {
            return new UserSnap("STUDENT", id, username, hash, display, major, year, false);
        }
        static UserSnap rep(String id, String username, String hash, String display, String company, boolean approved) {
            return new UserSnap("COMPANY_REP", id, username, hash, display, company, 0, approved);
        }
        static UserSnap staff(String id, String username, String hash, String display) {
            return new UserSnap("STAFF", id, username, hash, display, "", 0, false);
        }
    }
}