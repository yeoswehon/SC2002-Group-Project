package repositories;

import entities.users.User;

import java.util.List;
import java.util.Optional;
/**
 * Interface for UserRepository
 */
public interface UserRepository {
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    void save(User user);
    List<User> findAll();
}
