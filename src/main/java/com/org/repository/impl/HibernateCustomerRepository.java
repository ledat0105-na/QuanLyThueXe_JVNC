package com.org.repository.impl;

import com.org.config.HibernateUtil;
import com.org.entity.Customer;
import com.org.repository.CustomerRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

/**
 * Triển khai CustomerRepository bằng Hibernate.
 * Được dùng ở AuthService (đăng ký) và CustomerService (hồ sơ khách).
 */
public class HibernateCustomerRepository implements CustomerRepository {

  private final SessionFactory sessionFactory;

  public HibernateCustomerRepository() {
    this.sessionFactory = HibernateUtil.getSessionFactory();
  }

  @Override
  public Customer save(Customer customer) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.saveOrUpdate(customer);
      transaction.commit();
      return customer;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }

  @Override
  public Optional<Customer> findByEmail(String email) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Customer c WHERE c.email = :email", Customer.class)
          .setParameter("email", email)
          .uniqueResultOptional();
    }
  }

  @Override
  public Optional<Customer> findByIdentityCard(String identityCard) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Customer c WHERE c.identityCard = :identity", Customer.class)
          .setParameter("identity", identityCard)
          .uniqueResultOptional();
    }
  }

  @Override
  public Optional<Customer> findByAccountId(Long accountId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Customer c WHERE c.account.id = :accountId", Customer.class)
          .setParameter("accountId", accountId)
          .uniqueResultOptional();
    }
  }
}

