package com.org.entity;

import com.org.entity.id.CarRentalId;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CarRental")
public class CarRental implements Serializable {

  @EmbeddedId
  private CarRentalId id;

  @MapsId("customerId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CustomerID", nullable = false)
  private Customer customer;

  @MapsId("carId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CarID", nullable = false)
  private Car car;

  @Column(name = "ReturnDate", nullable = false)
  private LocalDate returnDate;

  @Column(name = "RentPrice", nullable = false, precision = 10, scale = 2)
  private BigDecimal rentPrice;

  @Column(name = "Status", nullable = false, length = 50)
  private String status;

  public CarRental() {
  }

  public CarRental(CarRentalId id, Customer customer, Car car) {
    this.id = id;
    this.customer = customer;
    this.car = car;
  }

  public CarRentalId getId() {
    return id;
  }

  public void setId(CarRentalId id) {
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

  public LocalDate getPickupDate() {
    return id != null ? id.getPickupDate() : null;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  public BigDecimal getRentPrice() {
    return rentPrice;
  }

  public void setRentPrice(BigDecimal rentPrice) {
    this.rentPrice = rentPrice;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

