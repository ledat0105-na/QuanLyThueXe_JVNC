package com.org.repository.impl;
import com.org.config.HibernateUtil;
import com.org.entity.CarRental;
import com.org.entity.id.CarRentalId;
import com.org.repository.CarRentalRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public class HibernateCarRentalRepository implements CarRentalRepository {
  private final SessionFactory sessionFactory;
  public HibernateCarRentalRepository() {
    this.sessionFactory = HibernateUtil.getSessionFactory();
  }
  @Override
  public CarRental save(CarRental carRental) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      com.org.entity.Customer managedCustomer = null;
      if (carRental.getCustomer() != null && carRental.getCustomer().getId() != null) {
        managedCustomer = (com.org.entity.Customer) session.merge(carRental.getCustomer());
      }
      com.org.entity.Car managedCar = null;
      if (carRental.getCar() != null && carRental.getCar().getId() != null) {
        managedCar = (com.org.entity.Car) session.merge(carRental.getCar());
      }
      CarRental existingRental = null;
      if (carRental.getId() != null) {
        existingRental = session.find(CarRental.class, carRental.getId());
      }
      CarRental savedRental;
      if (existingRental != null) {
        existingRental.setReturnDate(carRental.getReturnDate());
        existingRental.setRentPrice(carRental.getRentPrice());
        existingRental.setStatus(carRental.getStatus());
        if (managedCustomer != null) {
          existingRental.setCustomer(managedCustomer);
        }
        if (managedCar != null) {
          existingRental.setCar(managedCar);
        }
        savedRental = (CarRental) session.merge(existingRental);
      } else {
        if (managedCustomer != null) {
          carRental.setCustomer(managedCustomer);
        }
        if (managedCar != null) {
          carRental.setCar(managedCar);
        }
        session.persist(carRental);
        savedRental = carRental;
      }
      session.flush();
      transaction.commit();
      return savedRental;
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw e;
    }
  }
  @Override
  public Optional<CarRental> findById(CarRentalId id) {
    try (Session session = sessionFactory.openSession()) {
      CarRental rental = session.createQuery(
          "SELECT r FROM CarRental r " +
              "LEFT JOIN FETCH r.customer " +
              "LEFT JOIN FETCH r.car " +
              "WHERE r.id.customerId = :customerId " +
              "AND r.id.carId = :carId " +
              "AND r.id.pickupDate = :pickupDate",
          CarRental.class)
          .setParameter("customerId", id.getCustomerId())
          .setParameter("carId", id.getCarId())
          .setParameter("pickupDate", id.getPickupDate())
          .uniqueResult();
      return Optional.ofNullable(rental);
    }
  }
  @Override
  public List<CarRental> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "SELECT DISTINCT r FROM CarRental r " +
              "LEFT JOIN FETCH r.customer " +
              "LEFT JOIN FETCH r.car " +
              "ORDER BY r.id.pickupDate DESC",
          CarRental.class)
          .getResultList();
    }
  }
  @Override
  public void delete(CarRental carRental) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      CarRental mergedRental = (CarRental) session.merge(carRental);
      session.delete(mergedRental);
      session.flush();
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }
  @Override
  public List<CarRental> findByDateRange(LocalDate startDate, LocalDate endDate) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "SELECT DISTINCT r FROM CarRental r " +
              "LEFT JOIN FETCH r.customer " +
              "LEFT JOIN FETCH r.car " +
              "WHERE r.id.pickupDate BETWEEN :startDate AND :endDate " +
              "ORDER BY r.id.pickupDate DESC",
          CarRental.class)
          .setParameter("startDate", startDate)
          .setParameter("endDate", endDate)
          .getResultList();
    }
  }
  @Override
  public List<CarRental> findByStatus(String status) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "SELECT DISTINCT r FROM CarRental r " +
              "LEFT JOIN FETCH r.customer " +
              "LEFT JOIN FETCH r.car " +
              "WHERE r.status = :status " +
              "ORDER BY r.id.pickupDate DESC",
          CarRental.class)
          .setParameter("status", status)
          .getResultList();
    }
  }
  @Override
  public List<CarRental> findByCustomerId(Long customerId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "SELECT DISTINCT r FROM CarRental r " +
              "LEFT JOIN FETCH r.customer " +
              "LEFT JOIN FETCH r.car " +
              "WHERE r.id.customerId = :customerId " +
              "ORDER BY r.id.pickupDate DESC",
          CarRental.class)
          .setParameter("customerId", customerId)
          .getResultList();
    }
  }
  @Override
  public List<CarRental> findByCarId(Long carId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "SELECT DISTINCT r FROM CarRental r " +
              "LEFT JOIN FETCH r.customer " +
              "LEFT JOIN FETCH r.car " +
              "WHERE r.id.carId = :carId " +
              "ORDER BY r.id.pickupDate DESC",
          CarRental.class)
          .setParameter("carId", carId)
          .getResultList();
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
