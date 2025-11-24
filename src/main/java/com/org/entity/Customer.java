package com.org.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Customer")
public class Customer implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CustomerID")
  private Long id;

  @Column(name = "CustomerName", nullable = false, length = 150)
  private String customerName;

  @Column(name = "Mobile", nullable = false, length = 30)
  private String mobile;

  @Column(name = "Birthday", nullable = false)
  private LocalDate birthday;

  @Column(name = "IdentityCard", nullable = false, unique = true, length = 50)
  private String identityCard;

  @Column(name = "Email", nullable = false, unique = true, length = 150)
  private String email;

  @Column(name = "Password", nullable = false, length = 255)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "AccountID", nullable = false)
  private Account account;

  @OneToMany(mappedBy = "customer", orphanRemoval = false)
  private Set<CarRental> rentals = new LinkedHashSet<>();

  @OneToMany(mappedBy = "customer", orphanRemoval = false)
  private Set<Review> reviews = new LinkedHashSet<>();

  public Customer() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public String getIdentityCard() {
    return identityCard;
  }

  public void setIdentityCard(String identityCard) {
    this.identityCard = identityCard;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
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

