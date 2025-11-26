package com.org.entity;
import com.org.entity.id.ReviewId;
import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "Review")
public class Review implements Serializable {
  @EmbeddedId
  private ReviewId id;
  @MapsId("customerId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CustomerID", nullable = false)
  private Customer customer;
  @MapsId("carId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CarID", nullable = false)
  private Car car;
  @Column(name = "ReviewStar", nullable = false)
  private Integer reviewStar;
  @Column(name = "Comment", nullable = false, length = 500)
  private String comment;
  public Review() {
  }
  public Review(ReviewId id, Customer customer, Car car) {
    this.id = id;
    this.customer = customer;
    this.car = car;
  }
  public ReviewId getId() {
    return id;
  }
  public void setId(ReviewId id) {
    this.id = id;
  }
  public Customer getCustomer() {
    return customer;
  }
  public void setCustomer(Customer customer) {
    this.customer = customer;
  }
  public Car getCar() {
    return car;
  }
  public void setCar(Car car) {
    this.car = car;
  }
  public Integer getReviewStar() {
    return reviewStar;
  }
  public void setReviewStar(Integer reviewStar) {
    this.reviewStar = reviewStar;
  }
  public String getComment() {
    return comment;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }
}
