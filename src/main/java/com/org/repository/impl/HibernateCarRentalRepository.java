package com.org.repository.impl;

import com.org.config.HibernateUtil;
import com.org.entity.CarRental;
import com.org.repository.CarRentalRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

/**
 * Triển khai CarRentalRepository bằng Hibernate.
 * Mỗi method mở 1 Session riêng để làm việc với DB.
 */
public class HibernateCarRentalRepository implements CarRentalRepository {

  private final SessionFactory sessionFactory;

  public HibernateCarRentalRepository() {
    this.sessionFactory = HibernateUtil.getSessionFactory();
  }

  @Override
  public List<CarRental> findByCustomerId(Long customerId) {
    try (Session session = sessionFactory.openSession()) {
      // Lấy toàn bộ giao dịch thuê của 1 khách, mới nhất đứng trước
      return session.createQuery(
              "FROM CarRental cr WHERE cr.customer.id = :customerId ORDER BY cr.id.pickupDate DESC",
              CarRental.class)
          .setParameter("customerId", customerId)
          .list();
    }
  }

  @Override
  public Optional<CarRental> findByCustomerAndCar(Long customerId, Long carId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
              "FROM CarRental cr WHERE cr.customer.id = :customerId AND cr.car.id = :carId",
              CarRental.class)
          .setParameter("customerId", customerId)
          .setParameter("carId", carId)
          .setMaxResults(1)
          .uniqueResultOptional();
    }
  }
}

