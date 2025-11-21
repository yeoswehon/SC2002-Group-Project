package repositories.serializedRepositories;

import entities.common.Visibility;
import entities.posting.*;
import repositories.PostingRepository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Class for serialized Posting Repository
 */
public final class SerializedPostingRepository implements PostingRepository {

    private final Map<String, InternshipPosting> byId = new LinkedHashMap<>();
    private final Path serFile;
    /**
     * Constructor with path to serialized file
     */
    public SerializedPostingRepository() {
        this(Paths.get("DataStorage/SerializedRepos/PostingRepo.ser"));
    }
    /**
     * Constructor to general serialized file path
     */
    public SerializedPostingRepository(Path serFile) {
        this.serFile = serFile;
        load();
    }
    /**
     * Save posting to repository
     */
    @Override public void save(InternshipPosting posting) {
        byId.put(posting.getId(), posting);
        persist();
    }
    /**
     * Find posting by ID
     */
    @Override public Optional<InternshipPosting> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }
    /**
     * Find posting by company
     */
    @Override public List<InternshipPosting> findByCompany(String companyRepId) {
        return byId.values().stream()
                .filter(p -> p.getCompanyRepId().equals(companyRepId))
                .collect(Collectors.toList());
    }
    /**
     * Simple filter for posting
     */
    @Override public List<InternshipPosting> findVisibleByMajorAndWindow(Visibility vis, Major major, LocalDate today) {
        return byId.values().stream()
                .filter(p -> {
                    try { return p.getVisibility() == vis; } catch (Throwable t) { return true; } 
                })
                .filter(p -> {
                    try { return p.getStatus() == PostingStatus.APPROVED; } catch (Throwable t) { return true; }
                })
                .filter(p -> p.getMajor() == major)
                .filter(p -> !p.getOpenDate().isAfter(today) && !p.getCloseDate().isBefore(today))
                .collect(Collectors.toList());
    }
    /**
     * Get all postings
     */
    @Override public List<InternshipPosting> findAll() {
        return List.copyOf(byId.values());
    }

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
     * Snapshot class to save posting attributes
     */
    private Snapshots snapshot() {
        List<PostingSnap> items = new ArrayList<>();
        for (InternshipPosting p : byId.values()) {
            items.add(new PostingSnap(
                    p.getId(),
                    p.getCompanyRepId(),
                    p.getTitle(),
                    p.getDescription(),
                    p.getLevel(),
                    p.getMajor(),
                    p.getOpenDate(),
                    p.getCloseDate(),
                    p.getSlots(),
                    p.getConfirmed(),
                    p.getStatus(),
                    p.getVisibility()
            ));
        }
        return new Snapshots(items);
    }
    /**
     * Convert snapshot back to postings
     */
    private void inflate(Snapshots s) {
        byId.clear();
        for (PostingSnap p : s.items) {
            InternshipPosting x = new InternshipPosting(
                    p.id,
                    p.companyRepId,
                    p.title,
                    p.description,
                    p.level,
                    p.major,
                    p.openDate,
                    p.closeDate,
                    p.slots,
                    p.confirmed,
                    p.status,
                    p.visibility
            );
            byId.put(x.getId(), x);
        }
    }

    /**
     * Class to store all posting item snapshot
     */
    private static final class Snapshots implements Serializable {
        private static final long serialVersionUID = 1L;
        final List<PostingSnap> items;
        Snapshots(List<PostingSnap> items) { this.items = items; }
    }
    /**
     * Class to for individual item snapshot
     */
    private static final class PostingSnap implements Serializable {
        private static final long serialVersionUID = 1L;

        final String id, companyRepId, title, description;
        final Level level;
        final Major major;
        final LocalDate openDate, closeDate;
        final int slots, confirmed;
        final PostingStatus status;
        final Visibility visibility;

        PostingSnap(String id, String companyRepId, String title, String description,
                    Level level, Major major, LocalDate openDate, LocalDate closeDate,
                    int slots, int confirmed, PostingStatus status, Visibility visibility) {
            this.id = id;
            this.companyRepId = companyRepId;
            this.title = title;
            this.description = description;
            this.level = level;
            this.major = major;
            this.openDate = openDate;
            this.closeDate = closeDate;
            this.slots = slots;
            this.confirmed = confirmed;
            this.status = status;
            this.visibility = visibility;
        }
    }
}