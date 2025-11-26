package com.org.service;
import com.org.entity.Account;
import com.org.entity.Customer;
import com.org.repository.AccountRepository;
import com.org.repository.CustomerRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public class CustomerService {
  private final CustomerRepository customerRepository;
  private final AccountRepository accountRepository;
  public CustomerService(CustomerRepository customerRepository, AccountRepository accountRepository) {
    this.customerRepository = customerRepository;
    this.accountRepository = accountRepository;
  }
  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
    this.accountRepository = null;
  }
  public Optional<Customer> findByAccount(Account account) {
    if (account == null || account.getId() == null) {
      return Optional.empty();
    }
    return customerRepository.findByAccountId(account.getId());
  }
  public Customer saveProfile(Customer customer) {
    return customerRepository.save(customer);
  }
  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }
  public Optional<Customer> getCustomerById(Long id) {
    return customerRepository.findById(id);
  }
  public Customer createCustomer(String customerName, String mobile, LocalDate birthday,
                                 String identityCard, String email, String password) {
    if (accountRepository == null) {
      throw new IllegalStateException("AccountRepository không được khởi tạo");
    }
    if (customerRepository.findByEmail(email).isPresent()) {
      throw new IllegalStateException("Email đã được sử dụng");
    }
    if (customerRepository.findByIdentityCard(identityCard).isPresent()) {
      throw new IllegalStateException("CMND/CCCD đã được sử dụng");
    }
    Account account = new Account();
    account.setAccountName(email); // Dùng email làm tên đăng nhập
    account.setPassword(password);
    account.setRole("Customer");
    account = accountRepository.save(account);
    Customer customer = new Customer();
    customer.setCustomerName(customerName);
    customer.setMobile(mobile);
    customer.setBirthday(birthday);
    customer.setIdentityCard(identityCard);
    customer.setEmail(email);
    customer.setPassword(password);
    customer.setAccount(account);
    return customerRepository.save(customer);
  }
  public Customer updateCustomer(Long id, String customerName, String mobile, LocalDate birthday,
                                 String identityCard, String email) {
    if (accountRepository == null) {
      throw new IllegalStateException("AccountRepository không được khởi tạo");
    }
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy khách hàng"));
    Account account = customer.getAccount();
    if (account == null) {
      throw new IllegalStateException("Không tìm thấy tài khoản của khách hàng");
    }
    customerRepository.findByEmail(email)
        .ifPresent(c -> {
          if (!c.getId().equals(id)) {
            throw new IllegalStateException("Email đã được sử dụng bởi khách hàng khác");
          }
        });
    customerRepository.findByIdentityCard(identityCard)
        .ifPresent(c -> {
          if (!c.getId().equals(id)) {
            throw new IllegalStateException("CMND/CCCD đã được sử dụng bởi khách hàng khác");
          }
        });
    customer.setCustomerName(customerName);
    customer.setMobile(mobile);
    customer.setBirthday(birthday);
    customer.setIdentityCard(identityCard);
    customer.setEmail(email);
    if (!account.getAccountName().equals(email)) {
      account.setAccountName(email);
      accountRepository.save(account);
    }
    return customerRepository.save(customer);
  }
  public void deleteCustomer(Long id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy khách hàng"));
    customerRepository.delete(customer);
  }
}
