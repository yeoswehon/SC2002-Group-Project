package repositories;

import entities.application.Application;

import java.util.List;
import java.util.Optional;

/**
 * Interface for ApplicationRepository
 */
public interface ApplicationRepository {
    void save(Application app);
    Optional<Application> findById(String id);
    List<Application> findByStudent(String studentId);
    List<Application> findByPosting(String postingId);
    List<Application> findAll();
}
