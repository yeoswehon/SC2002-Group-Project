package services;

import entities.posting.InternshipPosting;

import java.util.Optional;
/**
 * Interface for Posting Lookup Service
 */
public interface PostingLookupService {
    Optional<InternshipPosting> findPostingById(String id);
}
