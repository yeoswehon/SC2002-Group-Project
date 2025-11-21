package services;

import entities.users.CompanyRep;
import entities.users.Staff;
import entities.users.Student;

import java.util.Optional;
/**
 * Interface for User Lookup Service
 */
public interface UserLookupService {
    Optional<Student> findStudentById(String id);
    Optional<Student> findStudentByUsername(String username);

    Optional<Staff> findStaffById(String id);
    Optional<Staff> findStaffByUsername(String username);

    Optional<CompanyRep> findRepById(String id);
    Optional<CompanyRep> findRepByUsername(String username);
}
