package is25.onlyswapx.review.infraestructure;

import is25.onlyswapx.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByReviewedId(Long reviewedId);

    List<Review> findByReviewerId(Long reviewerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewed.id = :userId")
    Double findAverageRatingByUserId(@Param("userId") Long userId);

    boolean existsByReviewerIdAndReviewedId(Long reviewerId, Long reviewedId);
}