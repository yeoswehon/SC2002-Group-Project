package kernel;

import repositories.serializedRepositories.SerializedApplicationRepository;
import repositories.serializedRepositories.SerializedApprovalQueue;
import repositories.serializedRepositories.SerializedPostingRepository;
import repositories.serializedRepositories.SerializedUserRepository;
import repositories.ApplicationRepository;
import repositories.ApprovalQueue;
import repositories.PostingRepository;
import repositories.UserRepository;
import security.PasswordHasher;
import security.Sha256PasswordHasher;
import util.Clock;

/**
 * Startup class for all services
 */
public final class Startup {
    /**
     * Method to startup all services
     */
    public static void init() {
        Clock clock = Clock.system();
        PasswordHasher hasher = new Sha256PasswordHasher();

        UserRepository users = new SerializedUserRepository(hasher);
        PostingRepository postings = new SerializedPostingRepository();
        ApplicationRepository apps = new SerializedApplicationRepository();
        ApprovalQueue queue = new SerializedApprovalQueue();

        AppContext.setServices(new ServiceRegistryImpl(users, postings, apps, queue, hasher, clock));
    }
}