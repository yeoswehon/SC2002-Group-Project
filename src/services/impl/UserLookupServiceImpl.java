package services.impl;

import entities.users.CompanyRep;
import entities.users.Staff;
import entities.users.Student;
import repositories.UserRepository;
import services.UserLookupService;

import java.util.Optional;
/**
 * Concrete implementation of User Lookup Service
 */
public class UserLookupServiceImpl implements UserLookupService {
    private final UserRepository users;

    public UserLookupServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override public Optional<Student> findStudentById(String id) {
        return users.findById(id).map(u -> (u instanceof Student) ? (Student) u : null);
    }
    @Override public Optional<Student> findStudentByUsername(String username) {
        return users.findByUsername(username).map(u -> (u instanceof Student) ? (Student) u : null);
    }

    @Override public Optional<Staff> findStaffById(String id) {
        return users.findById(id).map(u -> (u instanceof Staff) ? (Staff) u : null);
    }
    @Override public Optional<Staff> findStaffByUsername(String username) {
        return users.findByUsername(username).map(u -> (u instanceof Staff) ? (Staff) u : null);
    }

    @Override public Optional<CompanyRep> findRepById(String id) {
        return users.findById(id).map(u -> (u instanceof CompanyRep) ? (CompanyRep) u : null);
    }
    @Override public Optional<CompanyRep> findRepByUsername(String username) {
        return users.findByUsername(username).map(u -> (u instanceof CompanyRep) ? (CompanyRep) u : null);
    }

}
