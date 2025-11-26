package com.org.repository;

import com.org.entity.CarProducer;

import java.util.List;
import java.util.Optional;

public interface CarProducerRepository {

  CarProducer save(CarProducer producer);

  Optional<CarProducer> findById(Long id);

  List<CarProducer> findAll();

  void delete(CarProducer producer);

  boolean hasCars(Long producerId);
}

