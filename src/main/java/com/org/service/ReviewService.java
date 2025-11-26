package com.org.service;
import com.org.entity.CarRental;
import com.org.entity.Customer;
import com.org.entity.Review;
import com.org.entity.id.ReviewId;
import com.org.repository.CarRentalRepository;
import com.org.repository.ReviewRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public class ReviewService {
  private final ReviewRepository reviewRepository;
  private final CarRentalRepository carRentalRepository;
  public ReviewService(ReviewRepository reviewRepository,
                       CarRentalRepository carRentalRepository) {
    this.reviewRepository = reviewRepository;
    this.carRentalRepository = carRentalRepository;
  }
  public List<Review> findReviews(Customer customer) {
    if (customer == null || customer.getId() == null) {
      return Collections.emptyList();
    }
    return reviewRepository.findByCustomerId(customer.getId());
  }
  public Review submitReview(Customer customer,
                             Long carId,
                             int reviewStar,
                             String comment) {
    validateReviewRequest(customer, carId, reviewStar, comment);
    CarRental rental = carRentalRepository
        .findByCustomerAndCar(customer.getId(), carId)
        .orElseThrow(() -> new IllegalStateException("Khách chưa thuê xe này"));
    ReviewId id = new ReviewId(customer.getId(), rental.getCar().getId());
    Review review = reviewRepository.findById(id).orElseGet(() -> {
      Review newReview = new Review();
      newReview.setId(id);
      newReview.setCustomer(customer);
      newReview.setCar(rental.getCar());
      return newReview;
    });
    review.setReviewStar(reviewStar);
    review.setComment(comment);
    return reviewRepository.save(review);
  }
  private void validateReviewRequest(Customer customer, Long carId, int star, String comment) {
    if (customer == null || customer.getId() == null) {
      throw new IllegalArgumentException("Thiếu thông tin khách hàng");
    }
    if (carId == null) {
      throw new IllegalArgumentException("Thiếu thông tin xe");
    }
    if (star < 1 || star > 5) {
      throw new IllegalArgumentException("Số sao phải nằm trong khoảng 1-5");
    }
    if (comment == null || comment.isBlank()) {
      throw new IllegalArgumentException("Chưa nhập nội dung đánh giá");
    }
  }
}
