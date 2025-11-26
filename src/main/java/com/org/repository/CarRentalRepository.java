package com.org.repository;
import com.org.entity.CarRental;
import com.org.entity.id.CarRentalId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface CarRentalRepository {
  CarRental save(CarRental carRental);
  Optional<CarRental> findById(CarRentalId id);
  List<CarRental> findAll();
  void delete(CarRental carRental);
  List<CarRental> findByDateRange(LocalDate startDate, LocalDate endDate);
  List<CarRental> findByStatus(String status);
  List<CarRental> findByCustomerId(Long customerId);
  List<CarRental> findByCarId(Long carId);
  Optional<CarRental> findByCustomerAndCar(Long customerId, Long carId);
}
