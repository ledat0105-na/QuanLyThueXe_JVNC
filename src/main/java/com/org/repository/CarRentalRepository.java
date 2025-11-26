package com.org.repository;

import com.org.entity.CarRental;

import java.util.List;
import java.util.Optional;

/**
 * Repository giao tiếp với bảng CarRental.
 * Chỉ khai báo interface, phần triển khai cụ thể nằm ở lớp HibernateCarRentalRepository.
 */
public interface CarRentalRepository {

  List<CarRental> findByCustomerId(Long customerId);

  Optional<CarRental> findByCustomerAndCar(Long customerId, Long carId);
}

