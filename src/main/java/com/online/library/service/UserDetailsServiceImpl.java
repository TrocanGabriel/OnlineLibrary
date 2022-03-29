package com.online.library.service;

import com.online.library.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = customerRepository.findByEmail(username)
                .map(customer -> new User(customer.getEmail(),customer.getPassword(), Collections.emptyList()))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return user;
    }
}
