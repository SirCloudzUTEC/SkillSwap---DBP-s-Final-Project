package is25.onlyswapx.review.domain;

import is25.onlyswapx.review.dto.ReviewRequest;
import is25.onlyswapx.review.dto.ReviewResponse;
import is25.onlyswapx.review.infraestructure.ReviewRepository;
import is25.onlyswapx.user.domain.User;
import is25.onlyswapx.user.infraestructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(Long reviewerId, ReviewRequest request) {
        if (reviewerId.equals(request.getReviewedId())) {
            throw new RuntimeException("You cannot review yourself");
        }

        if (reviewRepository.existsByReviewerIdAndReviewedId(reviewerId, request.getReviewedId())) {
            throw new RuntimeException("You have already reviewed this user");
        }

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        User reviewed = userRepository.findById(request.getReviewedId())
                .orElseThrow(() -> new RuntimeException("Reviewed user not found"));

        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByReviewedId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Double getAverageRating(Long userId) {
        Double avg = reviewRepository.findAverageRatingByUserId(userId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerId(review.getReviewer().getId())
                .reviewerName(review.getReviewer().getFullName())
                .reviewedId(review.getReviewed().getId())
                .reviewedName(review.getReviewed().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}