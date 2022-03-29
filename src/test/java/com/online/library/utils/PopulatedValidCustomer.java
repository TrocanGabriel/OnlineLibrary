package com.online.library.utils;

import com.online.library.model.Customer;

public class PopulatedValidCustomer extends Customer {

    public PopulatedValidCustomer() {
        setEmail("test@gmail.com");
        setId(1L);
        setName("test name");
    }
}
