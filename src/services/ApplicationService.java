package services;

import entities.application.Application;
import entities.users.CompanyRep;
import entities.users.Student;
import kernel.Result;

import java.util.List;
/**
 * Interface for Application Service
 */
public interface ApplicationService {
    Result<Application> apply(Student student, String postingId);

    List<Application> listForPosting(String postingId);

    List<Application> listForStudent(String studentId);

    Result<Void> reviewByCompany(CompanyRep rep, String applicationId, boolean approve);

    Result<Void> acceptOffer(String studentId, String applicationId);

    Result<Void> requestWithdrawal(String studentId, String applicationId, String reason);
}