package kernel;

import repositories.ApplicationRepository;
import repositories.ApprovalQueue;
import repositories.PostingRepository;
import repositories.UserRepository;
import security.PasswordHasher;
import services.*;
import services.impl.*;
import util.Clock;
/**
 * Class to wire up all services
 */
public final class ServiceRegistryImpl implements AppContext.ServiceRegistry {
    private final AuthService auth;
    private final PostingService postings;
    private final ApplicationService applications;
    private final ApprovalService approvals;
    private final ReportingService reports;
    private final RegistrationService registration;
    private final UserLookupService userLookup;
    private final PostingLookupService postingLookup;
    /**
     * Constructor for ServiceRegistry
     */
    public ServiceRegistryImpl(UserRepository users,
                               PostingRepository postingRepo,
                               ApplicationRepository appRepo,
                               ApprovalQueue approvalQueue,
                               PasswordHasher hasher,
                               Clock clock) {
        this.auth = new AuthServiceImpl(users, hasher);
        this.postings = new PostingServiceImpl(postingRepo, approvalQueue, clock);
        this.applications = new ApplicationServiceImpl(appRepo, postingRepo, users, approvalQueue, clock);
        this.approvals = new ApprovalServiceImpl(approvalQueue, users, postingRepo, appRepo, clock);
        this.reports = new ReportingServiceImpl(users, postingRepo, appRepo);
        this.registration = new RegistrationServiceImpl(users, approvalQueue, hasher);
        this.userLookup = new UserLookupServiceImpl(users);
        this.postingLookup = new PostingLookupServiceImpl(postingRepo);
    }
    /**
     * Connect to AuthService
     */
    @Override public AuthService auth() { return auth; }
    /**
     * Connect to PostingService
     */
    @Override public PostingService postings() { return postings; }
    /**
     * Connect to ApplicationService
     */
    @Override public ApplicationService applications() { return applications; }
    /**
     * Connect to ApprovalService
     */
    @Override public ApprovalService approvals() { return approvals; }
    /**
     * Connect to ReportingService
     */
    @Override public ReportingService reports() { return reports; }
    /**
     * Connect to RegistrationService
     */
    @Override public RegistrationService registration() { return registration; }
    /**
     * Connect to UserLookupService
     */
    @Override public UserLookupService userLookup() { return userLookup; }
    /**
     * Connect to PostingLookupService
     */
    @Override public PostingLookupService postingLookup() {return postingLookup; }
}