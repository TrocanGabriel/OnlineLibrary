package com.online.library.repository;

import com.online.library.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository  extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String username);

    Boolean existsByEmail(String email);
}
