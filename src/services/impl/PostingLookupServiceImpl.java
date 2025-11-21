package services.impl;

import java.util.Optional;

import entities.posting.InternshipPosting;
import repositories.PostingRepository;
import services.PostingLookupService;
/**
 * Concrete implementation of Posting Lookup Service
 */
public final class PostingLookupServiceImpl implements PostingLookupService {

    private final PostingRepository postings;

    public PostingLookupServiceImpl(PostingRepository postings) {
        this.postings = postings;
    }

    @Override
    public Optional<InternshipPosting> findPostingById(String id) {
        return postings.findById(id);
    }
}
