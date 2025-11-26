package com.org.repository.impl;

import com.org.config.HibernateUtil;
import com.org.entity.Car;
import com.org.repository.CarRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class HibernateCarRepository implements CarRepository {

  private final SessionFactory sessionFactory;

  public HibernateCarRepository() {
    this.sessionFactory = HibernateUtil.getSessionFactory();
  }

  @Override
  public Car save(Car car) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.saveOrUpdate(car);
      transaction.commit();
      return car;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }

  @Override
  public Optional<Car> findById(Long id) {
    try (Session session = sessionFactory.openSession()) {
      Car car = session.createQuery(
          "SELECT c FROM Car c LEFT JOIN FETCH c.producer WHERE c.id = :id", Car.class)
          .setParameter("id", id)
          .uniqueResult();
      return Optional.ofNullable(car);
    }
  }

  @Override
  public List<Car> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "SELECT DISTINCT c FROM Car c LEFT JOIN FETCH c.producer ORDER BY c.carName", Car.class)
          .getResultList();
    }
  }

  @Override
  public void delete(Car car) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.delete(car);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }

  @Override
  public boolean hasRentals(Long carId) {
    try (Session session = sessionFactory.openSession()) {
      Long count = session.createQuery(
          "SELECT COUNT(r) FROM CarRental r WHERE r.car.id = :carId", Long.class)
          .setParameter("carId", carId)
          .uniqueResult();
      return count != null && count > 0;
    }
  }
}

