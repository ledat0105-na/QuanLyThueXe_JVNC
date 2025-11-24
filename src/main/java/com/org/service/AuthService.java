package com.org.service;

import com.org.entity.Account;
import com.org.entity.Customer;
import com.org.repository.AccountRepository;
import com.org.repository.CustomerRepository;
import com.org.service.dto.RegistrationRequest;

import java.util.Optional;

public class AuthService {

  private final AccountRepository accountRepository;
  private final CustomerRepository customerRepository;

  public AuthService(AccountRepository accountRepository,
                     CustomerRepository customerRepository) {
    this.accountRepository = accountRepository;
    this.customerRepository = customerRepository;
  }

  public Optional<Account> login(String username, String password) {
    return accountRepository.findByAccountName(username)
        .filter(account -> account.getPassword().equals(password));
  }

  public Customer registerCustomer(RegistrationRequest request) {
    validateRegistration(request);

    Account account = new Account(request.getUsername(), request.getPassword(), "CUSTOMER");

    Customer customer = new Customer();
    customer.setCustomerName(request.getFullName());
    customer.setMobile(request.getMobile());
    customer.setBirthday(request.getBirthday());
    customer.setIdentityCard(request.getIdentityCard());
    customer.setEmail(request.getEmail());
    customer.setPassword(request.getPassword());
    customer.setAccount(account);

    return customerRepository.save(customer);
  }

  private void validateRegistration(RegistrationRequest request) {
    if ("admin".equalsIgnoreCase(request.getUsername())) {
      throw new IllegalStateException("Tên đăng nhập này dành riêng cho quản trị.");
    }
    accountRepository.findByAccountName(request.getUsername())
        .ifPresent(a -> {
          throw new IllegalStateException("Tên tài khoản đã tồn tại");
        });

    customerRepository.findByEmail(request.getEmail())
        .ifPresent(c -> {
          throw new IllegalStateException("Email đã được sử dụng");
        });

    customerRepository.findByIdentityCard(request.getIdentityCard())
        .ifPresent(c -> {
          throw new IllegalStateException("CMND/CCCD đã được sử dụng");
        });
  }
}

