package com.org.service;

import com.org.entity.Account;
import com.org.entity.Customer;
import com.org.repository.CustomerRepository;

import java.util.Optional;

/**
 * Service xử lý nghiệp vụ liên quan đến bảng Customer.
 * <p>
 * Controller không làm việc trực tiếp với repository mà gọi qua lớp này
 * để code dễ test và dễ tái sử dụng.
 */
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    // Repository được tiêm từ bên ngoài (ở DashboardController)
    // giúp tách biệt tầng truy cập dữ liệu với logic nghiệp vụ.
    this.customerRepository = customerRepository;
  }

  /**
   * Lấy khách hàng dựa trên tài khoản đã đăng nhập.
   */
  public Optional<Customer> findByAccount(Account account) {
    if (account == null || account.getId() == null) {
      return Optional.empty();
    }
    return customerRepository.findByAccountId(account.getId());
  }

  /**
   * Lưu lại thông tin hồ sơ sau khi người dùng chỉnh sửa.
   */
  public Customer saveProfile(Customer customer) {
    return customerRepository.save(customer);
  }
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

  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }

  public Optional<Customer> getCustomerById(Long id) {
    return customerRepository.findById(id);
  }

  public Customer createCustomer(String customerName, String mobile, LocalDate birthday,
                                 String identityCard, String email, String password) {
    // Kiểm tra email đã tồn tại
    if (customerRepository.findByEmail(email).isPresent()) {
      throw new IllegalStateException("Email đã được sử dụng");
    }

    // Kiểm tra CMND/CCCD đã tồn tại
    if (customerRepository.findByIdentityCard(identityCard).isPresent()) {
      throw new IllegalStateException("CMND/CCCD đã được sử dụng");
    }

    // Tạo Account cho khách hàng
    Account account = new Account();
    account.setAccountName(email); // Dùng email làm tên đăng nhập
    account.setPassword(password);
    account.setRole("Customer");
    account = accountRepository.save(account);

    // Tạo Customer
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
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Không tìm thấy khách hàng"));

    // Đảm bảo Account đã được load
    Account account = customer.getAccount();
    if (account == null) {
      throw new IllegalStateException("Không tìm thấy tài khoản của khách hàng");
    }

    // Kiểm tra email đã tồn tại (trừ chính khách hàng này)
    customerRepository.findByEmail(email)
        .ifPresent(c -> {
          if (!c.getId().equals(id)) {
            throw new IllegalStateException("Email đã được sử dụng bởi khách hàng khác");
          }
        });

    // Kiểm tra CMND/CCCD đã tồn tại (trừ chính khách hàng này)
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

    // Cập nhật AccountName nếu email thay đổi
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

