package repositories.serializedRepositories;

import repositories.ApprovalQueue;
import entities.approval.ApprovalItem;
import entities.approval.Type;

import java.io.*;
import java.nio.file.*;
import java.util.*;
/**
 * Class for serialized approval queue Repository
 */
public final class SerializedApprovalQueue implements ApprovalQueue {

    private final LinkedHashMap<String, ApprovalItem> byId = new LinkedHashMap<>();
    private final Path serFile;
    /**
     * Constructor with path to serialized file
     */
    public SerializedApprovalQueue() {
        this(Paths.get("DataStorage/SerializedRepos/ApprovalQueue.ser"));
    }
    /**
     * Constructor to general serialized file path
     */
    public SerializedApprovalQueue(Path serFile) {
        this.serFile = serFile;
        load();
    }
    /**
     * Save approval request to repository
     */
    @Override
    public void submit(ApprovalItem item) {
        byId.put(keyOf(item), item);
        persist();
    }
    /**
     * Get all approvals
     */
    @Override
    public List<ApprovalItem> list() {
        return List.copyOf(byId.values());
    }
    /**
     * Find approval by ID
     */
    @Override
    public Optional<ApprovalItem> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }
    /**
     * remove approval by ID
     */
    @Override
    public void remove(String id) {
        byId.remove(id);
        persist();
    }
    /**
     * Load serialized file
     */
    private void load() {
        if (!Files.exists(serFile)) return;

        try (var in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(serFile)))) {

            Object o = in.readObject();
            if (o instanceof Snapshots s) {
                byId.clear();
                for (ApprovalItemSnap snap : s.items) {
                    ApprovalItem item = fromSnap(snap);
                    byId.put(keyOf(item), item);
                }
            }
        } catch (Exception ignored) {

        }
    }
    /**
     * Save repository to serialized file
     */
    private void persist() {
        try {
            Files.createDirectories(serFile.getParent());

            List<ApprovalItemSnap> snaps = new ArrayList<>();
            for (ApprovalItem item : byId.values()) {
                snaps.add(toSnap(item));
            }

            try (var out = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(serFile)))) {
                out.writeObject(new Snapshots(snaps));
            }
        } catch (IOException ignored) {
        }
    }
    /**
     * Get approval item ID
     */
    private static String keyOf(ApprovalItem item) {
        return item.getId();
    }
    /**
     * Convert approval to snapshot
     */
    private static ApprovalItemSnap toSnap(ApprovalItem item) {
        return new ApprovalItemSnap(
                item.getId(),
                item.getType().name(),  
                item.getRefId(),
                item.getCreatedBy(),
                item.getNote()
        );
    }
    /**
     * Convert snapshot to approval item
     */
    private static ApprovalItem fromSnap(ApprovalItemSnap s) {
        return new ApprovalItem(
                s.id,
                Type.valueOf(s.type), 
                s.refId,
                s.createdBy,
                s.note
        );
    }

    private static final class Snapshots implements Serializable {
        private static final long serialVersionUID = 1L;

        final List<ApprovalItemSnap> items;

        Snapshots(List<ApprovalItemSnap> items) {
            this.items = items;
        }
    }

    private static final class ApprovalItemSnap implements Serializable {
        private static final long serialVersionUID = 1L;

        final String id;
        final String type;   
        final String refId;
        final String createdBy;
        final String note;

        ApprovalItemSnap(String id, String type, String refId, String createdBy, String note) {
            this.id = id;
            this.type = type;
            this.refId = refId;
            this.createdBy = createdBy;
            this.note = note;
        }
    }
}