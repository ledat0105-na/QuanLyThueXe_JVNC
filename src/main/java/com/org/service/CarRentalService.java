package com.org.service;
import com.org.entity.Car;
import com.org.entity.CarRental;
import com.org.entity.Customer;
import com.org.entity.id.CarRentalId;
import com.org.repository.CarRentalRepository;
import com.org.repository.CarRepository;
import com.org.repository.CustomerRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
public class CarRentalService {
  private final CarRentalRepository carRentalRepository;
  private final CustomerRepository customerRepository;
  private final CarRepository carRepository;
  public CarRentalService(CarRentalRepository carRentalRepository,
                          CustomerRepository customerRepository,
                          CarRepository carRepository) {
    this.carRentalRepository = carRentalRepository;
    this.customerRepository = customerRepository;
    this.carRepository = carRepository;
  }
  public CarRentalService(CarRentalRepository carRentalRepository) {
    this.carRentalRepository = carRentalRepository;
    this.customerRepository = null;
    this.carRepository = null;
  }
  public List<CarRental> findRentals(Customer customer) {
    if (customer == null || customer.getId() == null) {
      return Collections.emptyList();
    }
    return carRentalRepository.findByCustomerId(customer.getId());
  }
  public List<CarRental> getAllRentals() {
    return carRentalRepository.findAll();
  }
  public Optional<CarRental> getRentalById(CarRentalId id) {
    return carRentalRepository.findById(id);
  }
  public CarRental createRental(Long customerId, Long carId, LocalDate pickupDate,
                                 LocalDate returnDate, BigDecimal rentPrice, String status) {
    if (pickupDate.isAfter(returnDate) || pickupDate.isEqual(returnDate)) {
      throw new IllegalStateException("Ngày trả xe phải sau ngày nhận xe");
    }
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy khách hàng"));
    Car car = carRepository.findById(carId)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy xe"));
    if (isCarRentedInPeriod(carId, pickupDate, returnDate)) {
      throw new IllegalStateException("Xe đã được thuê trong khoảng thời gian này");
    }
    CarRentalId id = new CarRentalId(customerId, carId, pickupDate);
    if (carRentalRepository.findById(id).isPresent()) {
      throw new IllegalStateException("Giao dịch thuê xe này đã tồn tại");
    }
    CarRental rental = new CarRental();
    rental.setId(id);
    rental.setCustomer(customer);
    rental.setCar(car);
    rental.setReturnDate(returnDate);
    rental.setRentPrice(rentPrice);
    rental.setStatus(status);
    return carRentalRepository.save(rental);
  }
  public CarRental updateRental(CarRentalId id, LocalDate returnDate,
                                BigDecimal rentPrice, String status) {
    CarRental rental = carRentalRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy giao dịch thuê xe"));
    LocalDate pickupDate = rental.getPickupDate();
    if (returnDate.isBefore(pickupDate) || returnDate.isEqual(pickupDate)) {
      throw new IllegalStateException("Ngày trả xe phải sau ngày nhận xe");
    }
    rental.setReturnDate(returnDate);
    rental.setRentPrice(rentPrice);
    rental.setStatus(status);
    return carRentalRepository.save(rental);
  }
  public CarRental updateRentalStatus(CarRentalId id, String newStatus) {
    CarRental rental = carRentalRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy giao dịch thuê xe"));
    rental.setStatus(newStatus);
    return carRentalRepository.save(rental);
  }
  public void deleteRental(CarRentalId id) {
    CarRental rental = carRentalRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy giao dịch thuê xe"));
    carRentalRepository.delete(rental);
  }
  public List<CarRental> getRentalsByDateRange(LocalDate startDate, LocalDate endDate) {
    if (startDate.isAfter(endDate)) {
      throw new IllegalStateException("Ngày bắt đầu phải trước ngày kết thúc");
    }
    return carRentalRepository.findByDateRange(startDate, endDate);
  }
  public List<CarRental> getRentalsByStatus(String status) {
    return carRentalRepository.findByStatus(status);
  }
  public List<CarRental> getRentalsByCustomer(Long customerId) {
    return carRentalRepository.findByCustomerId(customerId);
  }
  public List<CarRental> getRentalsByCar(Long carId) {
    return carRentalRepository.findByCarId(carId);
  }
  private boolean isCarRentedInPeriod(Long carId, LocalDate pickupDate, LocalDate returnDate) {
    List<CarRental> existingRentals = carRentalRepository.findByCarId(carId);
    for (CarRental rental : existingRentals) {
      LocalDate existingPickup = rental.getPickupDate();
      LocalDate existingReturn = rental.getReturnDate();
      if (!"Đã hoàn thành".equals(rental.getStatus()) && 
          !"Đã hủy".equals(rental.getStatus())) {
        if (!(returnDate.isBefore(existingPickup) || pickupDate.isAfter(existingReturn))) {
          return true;
        }
      }
    }
    return false;
  }
  public List<CarRental> getRentalReport(LocalDate startDate, LocalDate endDate) {
    if (startDate != null && endDate != null) {
      return getRentalsByDateRange(startDate, endDate);
    }
    return getAllRentals();
  }
}
