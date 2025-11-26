package com.org.repository;
import com.org.entity.Account;
import java.util.Optional;
public interface AccountRepository {
  Optional<Account> findByAccountName(String accountName);
  Account save(Account account);
}
