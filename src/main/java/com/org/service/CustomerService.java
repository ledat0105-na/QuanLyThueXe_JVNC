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
}

