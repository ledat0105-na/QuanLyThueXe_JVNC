package com.org.service;
import com.org.entity.Car;
import com.org.entity.CarProducer;
import com.org.repository.CarProducerRepository;
import com.org.repository.CarRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public class CarService {
  private final CarRepository carRepository;
  private final CarProducerRepository producerRepository;
  public CarService(CarRepository carRepository, CarProducerRepository producerRepository) {
    this.carRepository = carRepository;
    this.producerRepository = producerRepository;
  }
  public List<Car> getAllCars() {
    return carRepository.findAll();
  }
  public Optional<Car> getCarById(Long id) {
    return carRepository.findById(id);
  }
  public Car createCar(String carName, Integer carModelYear, String color, Integer capacity,
                      String description, LocalDate importDate, BigDecimal rentPrice,
                      String status, Long producerId) {
    CarProducer producer = producerRepository.findById(producerId)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy nhà sản xuất"));
    Car car = new Car();
    car.setCarName(carName);
    car.setCarModelYear(carModelYear);
    car.setColor(color);
    car.setCapacity(capacity);
    car.setDescription(description);
    car.setImportDate(importDate);
    car.setRentPrice(rentPrice);
    car.setStatus(status);
    car.setProducer(producer);
    return carRepository.save(car);
  }
  public Car updateCar(Long id, String carName, Integer carModelYear, String color, Integer capacity,
                      String description, LocalDate importDate, BigDecimal rentPrice,
                      String status, Long producerId) {
    Car car = carRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy xe"));
    CarProducer producer = producerRepository.findById(producerId)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy nhà sản xuất"));
    car.setCarName(carName);
    car.setCarModelYear(carModelYear);
    car.setColor(color);
    car.setCapacity(capacity);
    car.setDescription(description);
    car.setImportDate(importDate);
    car.setRentPrice(rentPrice);
    car.setStatus(status);
    car.setProducer(producer);
    return carRepository.save(car);
  }
  public void deleteCar(Long id) {
    Car car = carRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy xe"));
    if (carRepository.hasRentals(id)) {
      throw new IllegalStateException("Không thể xóa xe vì xe đã có trong giao dịch thuê");
    }
    carRepository.delete(car);
  }
  public Car updateCarStatus(Long id, String newStatus) {
    Car car = carRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy xe"));
    if (!carRepository.hasRentals(id)) {
      throw new IllegalStateException("Xe chưa có trong giao dịch thuê nào. Vui lòng sử dụng chức năng chỉnh sửa thông tin.");
    }
    car.setStatus(newStatus);
    return carRepository.save(car);
  }
  public List<CarProducer> getAllProducers() {
    return producerRepository.findAll();
  }
}
