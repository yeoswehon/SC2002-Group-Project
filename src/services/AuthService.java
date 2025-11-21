package services;

import entities.users.CompanyRep;
import entities.users.Staff;
import entities.users.Student;
import kernel.Result;

import java.util.Optional;
/**
 * Interface for Auth Service
 */
public interface AuthService {
    Optional<Student> loginStudent(String username, String password);
    Optional<Staff> loginStaff(String username, String password);
    Optional<CompanyRep> loginRep(String username, String password);

    Result<Void> changePassword(String userId, String oldPassword, String newPassword1, String newPassword2);
}