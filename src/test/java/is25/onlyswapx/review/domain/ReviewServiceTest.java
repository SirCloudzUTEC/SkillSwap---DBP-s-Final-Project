package is25.onlyswapx.review.domain;

import is25.onlyswapx.review.dto.ReviewRequest;
import is25.onlyswapx.review.dto.ReviewResponse;
import is25.onlyswapx.review.infraestructure.ReviewRepository;
import is25.onlyswapx.user.domain.User;
import is25.onlyswapx.user.infraestructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ReviewService reviewService;

    @Test
    @DisplayName("createReview: crea review exitosamente")
    void createReview_success() {
        User reviewer = User.builder().id(1L).fullName("Pedro").build();
        User reviewed = User.builder().id(2L).fullName("Leonardo").build();

        ReviewRequest request = new ReviewRequest(2L, 5, "Excelente profesor");

        when(reviewRepository.existsByReviewerIdAndReviewedId(1L, 2L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(reviewer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reviewed));
        when(reviewRepository.save(any())).thenAnswer(i -> {
            Review r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        ReviewResponse response = reviewService.createReview(1L, request);

        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getReviewerName()).isEqualTo("Pedro");
        assertThat(response.getReviewedName()).isEqualTo("Leonardo");
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("createReview: lanza excepción si se revisa a sí mismo")
    void createReview_selfReview() {
        ReviewRequest request = new ReviewRequest(1L, 5, "Me doy 5");

        assertThatThrownBy(() -> reviewService.createReview(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cannot review yourself");
    }

    @Test
    @DisplayName("createReview: lanza excepción si ya existe review")
    void createReview_alreadyReviewed() {
        ReviewRequest request = new ReviewRequest(2L, 4, "Ya lo reseñé");

        when(reviewRepository.existsByReviewerIdAndReviewedId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.createReview(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already reviewed");
    }

    @Test
    @DisplayName("getAverageRating: retorna promedio correcto")
    void getAverageRating_success() {
        when(reviewRepository.findAverageRatingByUserId(2L)).thenReturn(4.5);

        Double avg = reviewService.getAverageRating(2L);

        assertThat(avg).isEqualTo(4.5);
    }

    @Test
    @DisplayName("getReviewsByUser: retorna lista de reviews")
    void getReviewsByUser_success() {
        User reviewer = User.builder().id(1L).fullName("Pedro").build();
        User reviewed = User.builder().id(2L).fullName("Leonardo").build();

        Review review = Review.builder()
                .id(1L)
                .reviewer(reviewer)
                .reviewed(reviewed)
                .rating(5)
                .comment("Muy bueno")
                .build();

        when(reviewRepository.findByReviewedId(2L)).thenReturn(List.of(review));

        List<ReviewResponse> responses = reviewService.getReviewsByUser(2L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRating()).isEqualTo(5);
    }
}