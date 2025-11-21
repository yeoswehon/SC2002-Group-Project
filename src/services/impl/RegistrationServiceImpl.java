package services.impl;

import entities.approval.ApprovalItem;
import entities.approval.Type;
import entities.users.CompanyRep;
import entities.users.Staff;
import entities.users.Student;
import entities.posting.Major;
import kernel.Result;
import repositories.ApprovalQueue;
import repositories.UserRepository;
import security.PasswordHasher;
import services.RegistrationService;
/**
 * Concrete implementation of Registration Service
 */
public final class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository users;
    private final ApprovalQueue approvals;
    private final PasswordHasher hasher;

    public RegistrationServiceImpl(UserRepository users, ApprovalQueue approvals, PasswordHasher hasher) {
        this.users = users;
        this.approvals = approvals;
        this.hasher = hasher;
    }

    @Override
    public Result<Student> registerStudent(String username, String rawPassword, String displayName, Major major, int year) {
        if (users.findByUsername(username).isPresent()) return Result.fail("Username taken");
        Student s = new Student(username, hasher.hash(rawPassword), displayName, major, year);
        users.save(s);
        return Result.ok(s);
    }

    @Override
    public Result<CompanyRep> registerCompanyRep(String username, String rawPassword, String displayName, String companyName) {
        if (users.findByUsername(username).isPresent()) return Result.fail("Username taken");
        CompanyRep r = new CompanyRep(username, hasher.hash(rawPassword), displayName, companyName);
        users.save(r);
        approvals.submit(new ApprovalItem(Type.COMPANY_REP_REG, r.getId(), r.getId(), "New representative"));
        return Result.ok(r);
    }

    @Override
    public Result<CompanyRep> registerCompanyRep(String username, String rawPassword1, String rawPassword2, String displayName, String companyName) {
        if (!rawPassword1.equals(rawPassword2)) {
            return Result.fail("Registration failed: Passwords do not match.");
        }
        return registerCompanyRep(username, rawPassword1, displayName, companyName);
    }

    @Override
    public Result<Staff> createStaff(String username, String rawPassword, String displayName) {
        if (users.findByUsername(username).isPresent()) return Result.fail("Email has been taken");
        Staff st = new Staff(username, hasher.hash(rawPassword), displayName);
        users.save(st);
        return Result.ok(st);
    }
}