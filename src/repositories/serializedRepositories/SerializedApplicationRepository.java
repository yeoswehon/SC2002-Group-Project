package repositories.serializedRepositories;

import entities.application.Application;
import entities.application.ApplicationStatus;
import repositories.ApplicationRepository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Class for serialized Application Repository
 */
public final class SerializedApplicationRepository implements ApplicationRepository {

    private final Map<String, Application> byId = new LinkedHashMap<>();
    private final Path serFile;
    /**
     * Constructor with path to serialized file
     */
    public SerializedApplicationRepository() {
        this(Paths.get("DataStorage/SerializedRepos/ApplicationRepo.ser"));
    }
    /**
     * Constructor to general serialized file path
     */
    public SerializedApplicationRepository(Path serFile) {
        this.serFile = serFile;
        load();
    }
    /**
     * Save application to repository
     */
    @Override public void save(Application app) {
        byId.put(app.getId(), app);
        persist();
    }
    /**
     * Find application by ID
     */
    @Override public Optional<Application> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }
    /**
     * Find application by student ID
     */
    @Override public List<Application> findByStudent(String studentId) {
        return byId.values().stream().filter(a -> a.getStudentId().equals(studentId)).collect(Collectors.toList());
    }
    /**
     * Find application by posting ID
     */
    @Override public List<Application> findByPosting(String postingId) {
        return byId.values().stream().filter(a -> a.getPostingId().equals(postingId)).collect(Collectors.toList());
    }
    /**
     * Get all applications
     */
    @Override public List<Application> findAll() { return List.copyOf(byId.values()); }
    /**
     * Load serialized file
     */
    private void load() {
        if (!Files.exists(serFile)) return;
        try (var in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(serFile)))) {
            Object o = in.readObject();
            if (o instanceof Snapshots s) inflate(s);
        } catch (Exception ignored) {}
    }
    /**
     * Save repository to serialized file
     */
    private void persist() {
        try {
            Files.createDirectories(serFile.getParent());
            try (var out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(serFile)))) {
                out.writeObject(snapshot());
            }
        } catch (IOException ignored) {}
    }
    /**
     * Snapshot class to save application attributes
     */
    private Snapshots snapshot() {
        List<AppSnap> items = new ArrayList<>();
        for (Application a : byId.values()) {
            items.add(new AppSnap(
                    a.getId(),
                    a.getStudentId(),
                    a.getPostingId(),
                    a.getStatus(),        
                    a.getAppliedOn(),  
                    a.getUpdatedOn()    
            ));
        }
        return new Snapshots(items);
    }
    /**
     * Convert snapshot back to application
     */
    private void inflate(Snapshots s) {
        byId.clear();
        for (AppSnap a : s.items) {
            LocalDate appliedOn = a.appliedOn;
            LocalDate updatedOn = (a.updatedOn != null ? a.updatedOn : appliedOn);

            Application app = new Application(
                    a.id,
                    a.studentId,
                    a.postingId,
                    a.status,
                    appliedOn,
                    updatedOn
            );

            byId.put(app.getId(), app);
        }
    }
    /**
     * Class to store all approval item snapshot
     */
    private static final class Snapshots implements Serializable {
        private static final long serialVersionUID = 1L;
        final List<AppSnap> items;
        Snapshots(List<AppSnap> items) { this.items = items; }
    }
    /**
     * Class to for individual item snapshot
     */
    private static final class AppSnap implements Serializable {
        private static final long serialVersionUID = 1L;

        final String id;
        final String studentId;
        final String postingId;
        final ApplicationStatus status;
        final LocalDate appliedOn;
        final LocalDate updatedOn; 

        AppSnap(String id,
                String studentId,
                String postingId,
                ApplicationStatus status,
                LocalDate appliedOn,
                LocalDate updatedOn) {
            this.id = id;
            this.studentId = studentId;
            this.postingId = postingId;
            this.status = status;
            this.appliedOn = appliedOn;
            this.updatedOn = updatedOn;
        }
    }
}