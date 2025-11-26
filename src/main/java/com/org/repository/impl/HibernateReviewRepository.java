package com.org.repository.impl;
import com.org.config.HibernateUtil;
import com.org.entity.Review;
import com.org.entity.id.ReviewId;
import com.org.repository.ReviewRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import java.util.Optional;
public class HibernateReviewRepository implements ReviewRepository {
  private final SessionFactory sessionFactory;
  public HibernateReviewRepository() {
    this.sessionFactory = HibernateUtil.getSessionFactory();
  }
  @Override
  public List<Review> findByCustomerId(Long customerId) {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery(
              "FROM Review r WHERE r.customer.id = :customerId ORDER BY r.id.carId DESC",
              Review.class)
          .setParameter("customerId", customerId)
          .list();
    }
  }
  @Override
  public Optional<Review> findById(ReviewId id) {
    try (Session session = sessionFactory.openSession()) {
      return Optional.ofNullable(session.get(Review.class, id));
    }
  }
  @Override
  public Review save(Review review) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.saveOrUpdate(review);
      transaction.commit();
      return review;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }
}
