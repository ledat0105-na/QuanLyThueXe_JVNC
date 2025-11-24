package com.org.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "CarProducer")
public class CarProducer implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ProducerID")
  private Long id;

  @Column(name = "ProducerName", nullable = false, length = 150)
  private String producerName;

  @Column(name = "Address", nullable = false, length = 255)
  private String address;

  @Column(name = "Country", nullable = false, length = 100)
  private String country;

  @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL, orphanRemoval = false)
  private Set<Car> cars = new LinkedHashSet<>();

  public CarProducer() {
  }

  public CarProducer(String producerName, String address, String country) {
    this.producerName = producerName;
    this.address = address;
    this.country = country;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProducerName() {
    return producerName;
  }

  public void setProducerName(String producerName) {
    this.producerName = producerName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Set<Car> getCars() {
    return cars;
  }

  public void setCars(Set<Car> cars) {
    this.cars = cars;
  }
}

