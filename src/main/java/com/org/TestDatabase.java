package com.org;

import com.org.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple smoke test to verify Hibernate can connect to the configured database.
 */
public final class TestDatabase {

  private static final Logger LOGGER = Logger.getLogger(TestDatabase.class.getName());

  private TestDatabase() {
  }

  public static void main(String[] args) {
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    boolean success = false;

    try (Session session = sessionFactory.openSession()) {
      session.beginTransaction();
      // run a lightweight query (no-op) just to ensure connection is alive
      session.createNativeQuery("SELECT 1").getSingleResult();
      session.getTransaction().commit();
      success = true;
      LOGGER.info("Kết nối cơ sở dữ liệu thành công.");
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE, "Kết nối cơ sở dữ liệu thất bại.", ex);
    } finally {
      if (sessionFactory != null && !sessionFactory.isClosed()) {
        sessionFactory.close();
      }
    }

    if (!success) {
      System.exit(1);
    }
  }
}

