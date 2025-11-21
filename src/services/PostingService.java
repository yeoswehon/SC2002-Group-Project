package services;

import entities.posting.InternshipPosting;
import entities.users.CompanyRep;
import entities.users.Student;
import kernel.Result;

import java.time.LocalDate;
import java.util.List;

import entities.posting.Level;
import entities.posting.Major;
/**
 * Interface for Posting Service
 */
public interface PostingService {
    List<InternshipPosting> listVisibleForStudent(Student s);

    Result<InternshipPosting> createDraft(
            CompanyRep owner,
            String title,
            String description,
            Level level,     
            Major major,       
            LocalDate openDate,
            LocalDate closeDate,
            int slots);
    
    Result<Void> updateDraft(
        CompanyRep owner, String postingId,
        String title, String description,
        Level level, Major major,
        LocalDate openDate, LocalDate closeDate,
        int slots);

    Result<Void> submitForApproval(CompanyRep owner, String postingId);
    List<InternshipPosting> listByCompany(String companyRepId);
    Result<Void> toggleVisibility(CompanyRep owner, String postingId);
}