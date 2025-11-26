package com.org.repository;

import com.org.entity.Car;

import java.util.List;
import java.util.Optional;

public interface CarRepository {

  Car save(Car car);

  Optional<Car> findById(Long id);

  List<Car> findAll();

  void delete(Car car);

  boolean hasRentals(Long carId);
}

