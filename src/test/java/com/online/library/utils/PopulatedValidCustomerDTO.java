package com.online.library.utils;

import com.online.library.model.dto.CustomerDTO;

public class PopulatedValidCustomerDTO extends CustomerDTO {

    public PopulatedValidCustomerDTO() {
        setEmail("test@gmail.com");
        setId(1L);
        setName("test name");
    }
}
