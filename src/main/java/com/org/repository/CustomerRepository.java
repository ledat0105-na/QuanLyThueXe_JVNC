package com.org.repository;

import com.org.entity.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho bảng Customer.
 *  - Dùng ở AuthService để kiểm tra trùng email/CMND
 *  - Dùng ở CustomerService để lấy khách từ AccountID.
 */
public interface CustomerRepository {

  Customer save(Customer customer);

  Optional<Customer> findByEmail(String email);

  Optional<Customer> findByIdentityCard(String identityCard);

  Optional<Customer> findById(Long id);

  List<Customer> findAll();

  void delete(Customer customer);
}

