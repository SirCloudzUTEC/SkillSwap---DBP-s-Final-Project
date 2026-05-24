package com.onlyswapx.review.infrastructure;

import com.onlyswapx.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedId(Long reviewedId);
    boolean existsByReviewerIdAndReviewedId(Long reviewerId, Long reviewedId);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewed.id = :userId")
    Double findAverageRatingByUserId(Long userId);
}