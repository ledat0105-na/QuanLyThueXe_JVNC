package com.org;
import com.org.repository.*;
import com.org.repository.impl.*;
import com.org.service.AuthService;
import com.org.service.CarProducerService;
import com.org.service.CarRentalService;
import com.org.service.CarService;
import com.org.service.CustomerService;
public final class ServiceLocator {
  private static final AccountRepository ACCOUNT_REPOSITORY = new HibernateAccountRepository();
  private static final CustomerRepository CUSTOMER_REPOSITORY = new HibernateCustomerRepository();
  private static final CarRepository CAR_REPOSITORY = new HibernateCarRepository();
  private static final CarProducerRepository PRODUCER_REPOSITORY = new HibernateCarProducerRepository();
  private static final CarRentalRepository CAR_RENTAL_REPOSITORY = new HibernateCarRentalRepository();
  private static final AuthService AUTH_SERVICE = new AuthService(ACCOUNT_REPOSITORY, CUSTOMER_REPOSITORY);
  private static final CustomerService CUSTOMER_SERVICE = new CustomerService(CUSTOMER_REPOSITORY, ACCOUNT_REPOSITORY);
  private static final CarService CAR_SERVICE = new CarService(CAR_REPOSITORY, PRODUCER_REPOSITORY);
  private static final CarProducerService PRODUCER_SERVICE = new CarProducerService(PRODUCER_REPOSITORY);
  private static final CarRentalService CAR_RENTAL_SERVICE = new CarRentalService(CAR_RENTAL_REPOSITORY, CUSTOMER_REPOSITORY, CAR_REPOSITORY);
  private ServiceLocator() {
  }
  public static AuthService getAuthService() {
    return AUTH_SERVICE;
  }
  public static CustomerService getCustomerService() {
    return CUSTOMER_SERVICE;
  }
  public static CarService getCarService() {
    return CAR_SERVICE;
  }
  public static CarProducerService getCarProducerService() {
    return PRODUCER_SERVICE;
  }
  public static CarRentalService getCarRentalService() {
    return CAR_RENTAL_SERVICE;
  }
}
