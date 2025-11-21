package services.impl;

import entities.users.CompanyRep;
import entities.users.Role;
import entities.users.Staff;
import entities.users.Student;
import entities.users.User;
import kernel.Result;
import repositories.UserRepository;
import security.PasswordHasher;
import services.AuthService;

import java.util.Optional;
/**
 * Concrete implementation of Auth Service
 */
public final class AuthServiceImpl implements AuthService {
    private final UserRepository users;
    private final PasswordHasher hasher;

    public AuthServiceImpl(UserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    @Override public Optional<Student> loginStudent(String username, String password) {
        return users.findByUsername(username)
                .filter(u -> u.getRole() == Role.STUDENT)
                .filter(u -> hasher.matches(password, u.getPasswordHash()))
                .map(u -> (Student) u);
    }

    @Override public Optional<Staff> loginStaff(String username, String password) {
        return users.findByUsername(username)
                .filter(u -> u.getRole() == Role.STAFF)
                .filter(u -> hasher.matches(password, u.getPasswordHash()))
                .map(u -> (Staff) u);
    }

    @Override public Optional<CompanyRep> loginRep(String username, String password) {
        return users.findByUsername(username)
                .filter(u -> u.getRole() == Role.COMPANY_REP)
                .filter(u -> hasher.matches(password, u.getPasswordHash()))
                .map(u -> (CompanyRep) u)
                .filter(CompanyRep::isApproved); 
    }

    @Override public Result<Void> changePassword(String userId, String oldPassword, String newPassword1, String newPassword2) {
        Optional<User> u = users.findById(userId);
        if (u.isEmpty()) return Result.fail("User not found");
        if (!hasher.matches(oldPassword, u.get().getPasswordHash())) return Result.fail("Old password incorrect");
        if (!newPassword1.equals(newPassword2)) return Result.fail("New passwords don't match");
        u.get().setPasswordHash(hasher.hash(newPassword1));
        users.save(u.get());
        return Result.ok();
    }
}