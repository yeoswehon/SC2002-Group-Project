package services;

import entities.users.CompanyRep;
import entities.users.Staff;
import entities.users.Student;
import entities.posting.Major;
import kernel.Result;

import java.util.Optional;
/**
 * Interface for Registration Service
 */
public interface RegistrationService {
    Result<Student> registerStudent(String username, String rawPassword, String displayName, Major major, int year);
    Result<CompanyRep> registerCompanyRep(String username, String rawPassword, String displayName, String companyName);
    Result<CompanyRep> registerCompanyRep(String username, String rawPassword1, String rawPassword2, String displayName, String companyName);
    Result<Staff> createStaff(String username, String rawPassword, String displayName);
}