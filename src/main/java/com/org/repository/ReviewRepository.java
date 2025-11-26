package com.org.repository;

import com.org.entity.Review;
import com.org.entity.id.ReviewId;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho entity Review, dùng để đọc/ghi đánh giá của khách.
 */
public interface ReviewRepository {

  List<Review> findByCustomerId(Long customerId);

  Optional<Review> findById(ReviewId id);

  Review save(Review review);
}

