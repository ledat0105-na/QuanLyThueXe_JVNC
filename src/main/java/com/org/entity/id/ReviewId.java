package com.org.entity.id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReviewId implements Serializable {

  @Column(name = "CustomerID")
  private Long customerId;

  @Column(name = "CarID")
  private Long carId;

  public ReviewId() {
  }

  public ReviewId(Long customerId, Long carId) {
    this.customerId = customerId;
    this.carId = carId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getCarId() {
    return carId;
  }

  public void setCarId(Long carId) {
    this.carId = carId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReviewId reviewId = (ReviewId) o;
    return Objects.equals(customerId, reviewId.customerId)
        && Objects.equals(carId, reviewId.carId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerId, carId);
  }
}

