package com.org;

import com.org.repository.AccountRepository;
import com.org.repository.CustomerRepository;
import com.org.repository.impl.HibernateAccountRepository;
import com.org.repository.impl.HibernateCustomerRepository;
import com.org.service.AuthService;

public final class ServiceLocator {

  private static final AccountRepository ACCOUNT_REPOSITORY = new HibernateAccountRepository();
  private static final CustomerRepository CUSTOMER_REPOSITORY = new HibernateCustomerRepository();
  private static final AuthService AUTH_SERVICE = new AuthService(ACCOUNT_REPOSITORY, CUSTOMER_REPOSITORY);

  private ServiceLocator() {
  }

  public static AuthService getAuthService() {
    return AUTH_SERVICE;
  }
}

