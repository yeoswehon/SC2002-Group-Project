package entities.users;

import java.util.Objects;
/**
 * Class for Company Representative
 */
public final class CompanyRep extends User {
    private final String companyName;
    private boolean approved;
    /**
     * Constructor for new company representative
     */
    public CompanyRep(String username, String passwordHash, String displayName, String companyName) {
        super(Role.COMPANY_REP, passwordHash, displayName);
        setUsername(Objects.requireNonNull(username, "username must not be null"));
        this.companyName = Objects.requireNonNull(companyName, "companyName must not be null");
        this.approved = false;
    }
    /**
     * Constructor for loading company representative from serialized file
     */
    public CompanyRep(String id, String username, String passwordHash, String displayName,
                      String companyName) {
        super(id, Role.COMPANY_REP, passwordHash, displayName);
        setUsername(Objects.requireNonNull(username, "username must not be null"));
        this.companyName = Objects.requireNonNull(companyName, "companyName must not be null");
    }
    /**
     * Accessor for Company Rep's Company Name
     */
    public String getCompanyName() { return companyName; }
    /**
     * Accessor to see if company rep is approved
     */
    public boolean isApproved() { return approved; }
    /**
     * Mutator to change company rep approval status
     */
    public void setApproved(boolean approved) { this.approved = approved; }
}
