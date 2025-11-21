package entities.posting;

import entities.common.Visibility;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Internship posting class
 */
public final class InternshipPosting {
    private final String id;
    private final String companyRepId;

    private String title;
    private String description;
    private Level level;
    private Major major;
    private LocalDate openDate;
    private LocalDate closeDate;
    private PostingStatus status = PostingStatus.DRAFT;
    private Visibility visibility = Visibility.PUBLIC;
    private int slots;      
    private int confirmed;
    /**
     * Constructor when creating new internship posting
     */
    public InternshipPosting(String companyRepId,
                             String title,
                             String description,
                             Level level,
                             Major major,
                             LocalDate openDate,
                             LocalDate closeDate,
                             int slots) {
        this.id = UUID.randomUUID().toString();
        this.companyRepId = companyRepId;
        this.title = title;
        this.description = description;
        this.level = level;
        this.major = major;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.slots = slots;
    }
    /**
     * Constructor when loading internship posting from serialized file
     */
    public InternshipPosting(String id,
                             String companyRepId,
                             String title,
                             String description,
                             Level level,
                             Major major,
                             LocalDate openDate,
                             LocalDate closeDate,
                             int slots,
                             int confirmed,
                             PostingStatus status,
                             Visibility visibility) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.companyRepId = Objects.requireNonNull(companyRepId, "companyRepId must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.level = Objects.requireNonNull(level, "level must not be null");
        this.major = Objects.requireNonNull(major, "major must not be null");
        this.openDate = Objects.requireNonNull(openDate, "openDate must not be null");
        this.closeDate = Objects.requireNonNull(closeDate, "closeDate must not be null");
        this.slots = slots;
        this.confirmed = confirmed;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.visibility = Objects.requireNonNull(visibility, "visibility must not be null");
    }
    /**
     * Accessor for posting ID
     */
    public String getId() { return id; }
    /**
     * Accessor for posting's company rep ID
     */
    public String getCompanyRepId() { return companyRepId; }
    /**
     * Accessor for posting title
     */
    public String getTitle() { return title; }
    /**
     * Accessor for posting description
     */
    public String getDescription() { return description; }
    /**
     * Accessor for posting level
     */
    public Level getLevel() { return level; }
    /**
     * Accessor for posting major
     */
    public Major getMajor() { return major; }
    /**
     * Accessor for posting opening date
     */
    public LocalDate getOpenDate() { return openDate; }
    /**
     * Accessor for posting closing date
     */
    public LocalDate getCloseDate() { return closeDate; }
    /**
     * Accessor for posting status
     */
    public PostingStatus getStatus() { return status; }
    /**
     * Accessor for posting visibility
     */
    public Visibility getVisibility() { return visibility; }
    /**
     * Accessor for number of posting slots
     */
    public int getSlots() { return slots; }
    /**
     * Accessor for number of confirmed applicants
     */
    public int getConfirmed() { return confirmed; }
    /**
     * Mutator for posting title
     */
    public void setTitle(String title) { this.title = title; }
    /**
     * Mutator for posting description
     */
    public void setDescription(String description) { this.description = description; }
    /**
     * Mutator for posting level
     */
    public void setLevel(Level level) { this.level = level; }
    /**
     * Mutator for posting major
     */
    public void setMajor(Major major) { this.major = major; }
    /**
     * Mutator for posting opening and closing date
     */
    public void setDates(LocalDate open, LocalDate close) { this.openDate = open; this.closeDate = close; }
    /**
     * Mutator for posting visibility
     */
    public void setVisibility(Visibility v) { this.visibility = v; }
    /**
     * Mutator for posting status
     */
    public void setStatus(PostingStatus s) { this.status = s; }
    /**
     * Mutator for posting slots
     */
    public void setSlots(int slots) { this.slots = slots; }
    /**
     * Accessor for posting still has capacity
     */
    public boolean hasCapacity() { return confirmed < slots; }
    /**
     * Mutator after confirming applicant
     */
    public void confirmOne() { this.confirmed++; }
}
