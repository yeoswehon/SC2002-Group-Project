package repositories;

import entities.common.Visibility;
import entities.posting.InternshipPosting;
import entities.posting.Major;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
/**
 * Interface for PostingRepository
 */
public interface PostingRepository {
    void save(InternshipPosting posting);
    Optional<InternshipPosting> findById(String id);
    List<InternshipPosting> findByCompany(String companyRepId);
    List<InternshipPosting> findVisibleByMajorAndWindow(Visibility vis, Major major, LocalDate today);
    List<InternshipPosting> findAll();
}
