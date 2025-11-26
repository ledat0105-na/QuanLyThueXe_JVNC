package com.org.repository.impl;
import com.org.config.HibernateUtil;
import com.org.entity.Account;
import com.org.entity.Customer;
import com.org.repository.CustomerRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import java.util.Optional;
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
      if (customer.getAccount() != null && customer.getAccount().getId() != null) {
        Account mergedAccount = (Account) session.merge(customer.getAccount());
        customer.setAccount(mergedAccount);
      }
      session.saveOrUpdate(customer);
      session.flush(); // Đảm bảo dữ liệu được ghi vào database
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
      Customer customer = session.createQuery(
          "SELECT c FROM Customer c LEFT JOIN FETCH c.account WHERE c.email = :email", Customer.class)
          .setParameter("email", email)
          .uniqueResult();
      return Optional.ofNullable(customer);
    }
  }
  @Override
  public Optional<Customer> findByIdentityCard(String identityCard) {
    try (Session session = sessionFactory.openSession()) {
      Customer customer = session.createQuery(
          "SELECT c FROM Customer c LEFT JOIN FETCH c.account WHERE c.identityCard = :identity", Customer.class)
          .setParameter("identity", identityCard)
          .uniqueResult();
      return Optional.ofNullable(customer);
    }
  }
  @Override
  public Optional<Customer> findById(Long id) {
    try (Session session = sessionFactory.openSession()) {
      Customer customer = session.createQuery(
          "SELECT c FROM Customer c LEFT JOIN FETCH c.account WHERE c.id = :id", Customer.class)
          .setParameter("id", id)
          .uniqueResult();
      return Optional.ofNullable(customer);
    }
  }
  @Override
  public List<Customer> findAll() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
          "SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.account ORDER BY c.customerName", Customer.class)
          .getResultList();
    }
  }
  @Override
  public void delete(Customer customer) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.delete(customer);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
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
