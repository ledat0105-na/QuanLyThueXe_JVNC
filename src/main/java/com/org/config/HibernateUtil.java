package com.org.config;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public final class HibernateUtil {
  private static final SessionFactory SESSION_FACTORY = buildSessionFactory();
  private HibernateUtil() {
  }
  private static SessionFactory buildSessionFactory() {
    try {
      return new Configuration()
          .configure("hibernate.cfg.xml")
          .buildSessionFactory();
    } catch (Throwable ex) {
      throw new ExceptionInInitializerError("Initial SessionFactory creation failed " + ex);
    }
  }
  public static SessionFactory getSessionFactory() {
    return SESSION_FACTORY;
  }
}
