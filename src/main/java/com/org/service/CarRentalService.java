package com.org.service;

import com.org.entity.CarRental;
import com.org.entity.Customer;
import com.org.repository.CarRentalRepository;

import java.util.Collections;
import java.util.List;

/**
 * Service chuyên làm việc với lịch sử thuê xe của khách hàng.
 */
public class CarRentalService {

  private final CarRentalRepository carRentalRepository;

  public CarRentalService(CarRentalRepository carRentalRepository) {
    // Nhận repository thông qua constructor để dễ mock khi viết unit test.
    this.carRentalRepository = carRentalRepository;
  }

  /**
   * Lấy toàn bộ lịch sử thuê xe của khách.
   */
  public List<CarRental> findRentals(Customer customer) {
    if (customer == null || customer.getId() == null) {
      return Collections.emptyList();
    }
    return carRentalRepository.findByCustomerId(customer.getId());
  }
}

