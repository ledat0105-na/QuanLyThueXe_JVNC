package com.org.repository.impl;
import com.org.config.HibernateUtil;
import com.org.entity.CarProducer;
import com.org.repository.CarProducerRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import java.util.Optional;
public class HibernateCarProducerRepository implements CarProducerRepository {
  private final SessionFactory sessionFactory;
  public HibernateCarProducerRepository() {
    this.sessionFactory = HibernateUtil.getSessionFactory();
  }
  @Override
  public CarProducer save(CarProducer producer) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.saveOrUpdate(producer);
      transaction.commit();
      return producer;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }
  @Override
  public Optional<CarProducer> findById(Long id) {
    try (Session session = sessionFactory.openSession()) {
      return Optional.ofNullable(session.get(CarProducer.class, id));
    }
  }
  @Override
  public List<CarProducer> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM CarProducer p ORDER BY p.producerName", CarProducer.class)
          .getResultList();
    }
  }
  @Override
  public void delete(CarProducer producer) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.delete(producer);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }
  @Override
  public boolean hasCars(Long producerId) {
    try (Session session = sessionFactory.openSession()) {
      Long count = session.createQuery(
          "SELECT COUNT(c) FROM Car c WHERE c.producer.id = :producerId", Long.class)
          .setParameter("producerId", producerId)
          .uniqueResult();
      return count != null && count > 0;
    }
  }
}
