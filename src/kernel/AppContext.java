package kernel;

import services.*;

/**
 * Create singleton for app
 */
public final class AppContext {
    private static ServiceRegistry registry;

    private AppContext() {}
    /**
     * Method to set services
     */
    public static synchronized void setServices(ServiceRegistry r) {
        if (r == null) throw new IllegalArgumentException("ServiceRegistry cannot be null");
        registry = r;
    }
    /**
     * Method to set service registry
     */
    public static ServiceRegistry services() {
        if (registry == null) {
            throw new IllegalStateException("ServiceRegistry not initialized. Call AppContext.setServices(...) at startup.");
        }
        return registry;
    }
    /**
     * Interface to wire up all services
     */
    public interface ServiceRegistry {
        AuthService auth();
        PostingService postings();
        ApplicationService applications();
        ApprovalService approvals();
        ReportingService reports();
        RegistrationService registration();
        UserLookupService userLookup();
        PostingLookupService postingLookup();
    }
}
