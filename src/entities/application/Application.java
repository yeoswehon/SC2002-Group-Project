package entities.application;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
/**
 * Application of a student
 */
public final class Application {
    private final String id;
    private final String studentId;
    private final String postingId;
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private final LocalDate appliedOn;
    private LocalDate updatedOn;
    /**
     * Application Constructor for new application
     */
    public Application(String studentId, String postingId, LocalDate today) {
        this(UUID.randomUUID().toString(),
                studentId,
                postingId,
                ApplicationStatus.PENDING,
                today,
                today);
    }
    /**
     * Application Constructor for loading from serialized file
     */
    public Application(String id,
                       String studentId,
                       String postingId,
                       ApplicationStatus status,
                       LocalDate appliedOn,
                       LocalDate updatedOn) {

        this.id = Objects.requireNonNull(id, "id must not be null");
        this.studentId = Objects.requireNonNull(studentId, "studentId must not be null");
        this.postingId = Objects.requireNonNull(postingId, "postingId must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.appliedOn = Objects.requireNonNull(appliedOn, "appliedOn must not be null");
        this.updatedOn = Objects.requireNonNull(updatedOn, "updatedOn must not be null");
    }
    /**
     * Accessor for application ID
     */
    public String getId() { return id; }
    /**
     * Accessor for student ID
     */
    public String getStudentId() { return studentId; }
    /**
     * Accessor for posting ID
     */
    public String getPostingId() { return postingId; }
    /**
     * Accessor for application status
     */
    public ApplicationStatus getStatus() { return status; }
    /**
     * Accessor for when application was applied
     */
    public LocalDate getAppliedOn() { return appliedOn; }
    /**
     * Accessor for when application was updated
     */
    public LocalDate getUpdatedOn() { return updatedOn; }
    /**
     * Mutator to change application status
     */
    public void setStatus(ApplicationStatus s, LocalDate when) {
        this.status = s;
        this.updatedOn = when;
    }
}
