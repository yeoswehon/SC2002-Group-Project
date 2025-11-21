package services;

import entities.application.Application;
import entities.posting.InternshipPosting;

import java.util.List;
import java.util.Map;

import entities.posting.Major;
/**
 * Interface for Reporting Service
 */
public interface ReportingService {
    Map<Major, Long> countApplicationsByMajor(); 

    List<InternshipPosting> listApprovedPostings();
    List<Application> listSuccessfulApplications();
}
