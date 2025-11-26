package com.org.service.dto;
import java.time.LocalDate;
public class RegistrationRequest {
  private final String username;
  private final String password;
  private final String fullName;
  private final String mobile;
  private final LocalDate birthday;
  private final String identityCard;
  private final String email;
  public RegistrationRequest(String username, String password, String fullName, String mobile,
                             LocalDate birthday, String identityCard, String email) {
    this.username = username;
    this.password = password;
    this.fullName = fullName;
    this.mobile = mobile;
    this.birthday = birthday;
    this.identityCard = identityCard;
    this.email = email;
  }
  public String getUsername() {
    return username;
  }
  public String getPassword() {
    return password;
  }
  public String getFullName() {
    return fullName;
  }
  public String getMobile() {
    return mobile;
  }
  public LocalDate getBirthday() {
    return birthday;
  }
  public String getIdentityCard() {
    return identityCard;
  }
  public String getEmail() {
    return email;
  }
}
