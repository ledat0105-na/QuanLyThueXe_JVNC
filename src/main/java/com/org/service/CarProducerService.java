package com.org.service;

import com.org.entity.CarProducer;
import com.org.repository.CarProducerRepository;

import java.util.List;
import java.util.Optional;

public class CarProducerService {

  private final CarProducerRepository producerRepository;

  public CarProducerService(CarProducerRepository producerRepository) {
    this.producerRepository = producerRepository;
  }

  public List<CarProducer> getAllProducers() {
    return producerRepository.findAll();
  }

  public Optional<CarProducer> getProducerById(Long id) {
    return producerRepository.findById(id);
  }

  public CarProducer createProducer(String producerName, String address, String country) {
    CarProducer producer = new CarProducer();
    producer.setProducerName(producerName);
    producer.setAddress(address);
    producer.setCountry(country);

    return producerRepository.save(producer);
  }

  public CarProducer updateProducer(Long id, String producerName, String address, String country) {
    CarProducer producer = producerRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy nhà sản xuất"));

    producer.setProducerName(producerName);
    producer.setAddress(address);
    producer.setCountry(country);

    return producerRepository.save(producer);
  }

  public void deleteProducer(Long id) {
    CarProducer producer = producerRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy nhà sản xuất"));

    // Kiểm tra xem có xe nào đang sử dụng nhà sản xuất này không
    if (producerRepository.hasCars(id)) {
      throw new IllegalStateException("Không thể xóa nhà sản xuất vì còn xe đang sử dụng");
    }

    producerRepository.delete(producer);
  }
}

