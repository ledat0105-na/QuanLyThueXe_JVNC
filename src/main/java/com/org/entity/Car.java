package com.org.entity;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
@Entity
@Table(name = "Car")
public class Car implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CarID")
  private Long id;
  @Column(name = "CarName", nullable = false, length = 150)
  private String carName;
  @Column(name = "CarModelYear", nullable = false)
  private Integer carModelYear;
  @Column(name = "Color", nullable = false, length = 50)
  private String color;
  @Column(name = "Capacity", nullable = false)
  private Integer capacity;
  @Column(name = "Description", nullable = false, columnDefinition = "TEXT")
  private String description;
  @Column(name = "ImportDate", nullable = false)
  private LocalDate importDate;
  @Column(name = "RentPrice", nullable = false, precision = 10, scale = 2)
  private BigDecimal rentPrice;
  @Column(name = "Status", nullable = false, length = 50)
  private String status;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ProducerID", nullable = false)
  private CarProducer producer;
  @OneToMany(mappedBy = "car", orphanRemoval = false)
  private Set<CarRental> rentals = new LinkedHashSet<>();
  @OneToMany(mappedBy = "car", orphanRemoval = false)
  private Set<Review> reviews = new LinkedHashSet<>();
  public Car() {
  }
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getCarName() {
    return carName;
  }
  public void setCarName(String carName) {
    this.carName = carName;
  }
  public Integer getCarModelYear() {
    return carModelYear;
  }
  public void setCarModelYear(Integer carModelYear) {
    this.carModelYear = carModelYear;
  }
  public String getColor() {
    return color;
  }
  public void setColor(String color) {
    this.color = color;
  }
  public Integer getCapacity() {
    return capacity;
  }
  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public LocalDate getImportDate() {
    return importDate;
  }
  public void setImportDate(LocalDate importDate) {
    this.importDate = importDate;
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
  public CarProducer getProducer() {
    return producer;
  }
  public void setProducer(CarProducer producer) {
    this.producer = producer;
  }
  public Set<CarRental> getRentals() {
    return rentals;
  }
  public void setRentals(Set<CarRental> rentals) {
    this.rentals = rentals;
  }
  public Set<Review> getReviews() {
    return reviews;
  }
  public void setReviews(Set<Review> reviews) {
    this.reviews = reviews;
  }
}
