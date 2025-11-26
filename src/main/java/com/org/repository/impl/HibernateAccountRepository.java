package com.org.repository.impl;
import com.org.config.HibernateUtil;
import com.org.entity.Account;
import com.org.repository.AccountRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Optional;
public class HibernateAccountRepository implements AccountRepository {
  private final SessionFactory sessionFactory;
  public HibernateAccountRepository() {
    this.sessionFactory = HibernateUtil.getSessionFactory();
  }
  @Override
  public Optional<Account> findByAccountName(String accountName) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("FROM Account a WHERE a.accountName = :name", Account.class)
          .setParameter("name", accountName)
          .uniqueResultOptional();
    }
  }
  @Override
  public Account save(Account account) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.saveOrUpdate(account);
      transaction.commit();
      return account;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }
}
