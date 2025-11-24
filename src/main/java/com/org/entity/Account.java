package com.org.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Account")
public class Account implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "AccountID")
  private Long id;

  @Column(name = "AccountName", nullable = false, unique = true, length = 100)
  private String accountName;

  @Column(name = "Password", nullable = false, length = 255)
  private String password;

  @Column(name = "Role", nullable = false, length = 50)
  private String role;

  public Account() {
  }

  public Account(String accountName, String password, String role) {
    this.accountName = accountName;
    this.password = password;
    this.role = role;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

