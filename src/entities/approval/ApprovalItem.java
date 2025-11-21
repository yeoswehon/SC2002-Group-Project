package entities.approval;

import java.util.Objects;
import java.util.UUID;
/**
 * Approval request class, rep registration, posting approval and student withdrawal
 */
public final class ApprovalItem {
    private final String id;
    private final Type type;
    private final String refId;
    private final String createdBy;
    private final String note;
    /**
     * Constructor for new approval
     */
    public ApprovalItem(Type type, String refId, String createdBy, String note) {
        this(UUID.randomUUID().toString(), type, refId, createdBy, note);
    }
    /**
     * Constructor when loading from serialized file
     */
    public ApprovalItem(String id, Type type, String refId, String createdBy, String note) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.refId = Objects.requireNonNull(refId, "refId must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.note = note; 
    }
    /**
     * Accessor for Approval ID
     */
    public String getId()        { return id; }
    /**
     * Accessor for Approval Type
     */
    public Type getType()        { return type; }
    /**
     * Accessor for Reference ID
     */
    public String getRefId()     { return refId; }
    /**
     * Accessor to check who created the approval request
     */
    public String getCreatedBy() { return createdBy; }
    /**
     * Get the note in the approval item
     */
    public String getNote()      { return note; }
}
