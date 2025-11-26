package com.org.entity.id;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
@Embeddable
public class CarRentalId implements Serializable {
  @Column(name = "CustomerID")
  private Long customerId;
  @Column(name = "CarID")
  private Long carId;
  @Column(name = "PickupDate")
  private LocalDate pickupDate;
  public CarRentalId() {
  }
  public CarRentalId(Long customerId, Long carId, LocalDate pickupDate) {
    this.customerId = customerId;
    this.carId = carId;
    this.pickupDate = pickupDate;
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
  public LocalDate getPickupDate() {
    return pickupDate;
  }
  public void setPickupDate(LocalDate pickupDate) {
    this.pickupDate = pickupDate;
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CarRentalId that = (CarRentalId) o;
    return Objects.equals(customerId, that.customerId)
        && Objects.equals(carId, that.carId)
        && Objects.equals(pickupDate, that.pickupDate);
  }
  @Override
  public int hashCode() {
    return Objects.hash(customerId, carId, pickupDate);
  }
}
