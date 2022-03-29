package com.online.library.service;

import com.online.library.exception.RecordNotFoundException;
import com.online.library.model.Customer;
import com.online.library.model.dto.CustomerDTO;
import com.online.library.repository.CustomerRepository;
import com.online.library.utils.PopulatedValidCustomer;
import com.online.library.utils.PopulatedValidCustomerDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
public class CustomerServiceTest {

    private final Customer customer = new PopulatedValidCustomer();

    private final CustomerDTO customerDTO = new PopulatedValidCustomerDTO();

    @InjectMocks
    private final CustomerService customerService = new CustomerService();

    @MockBean
    private CustomerRepository customerRepository;

    @Before
    public void before(){
        initMocks(this);
    }

    @Test
    public void whenGettingCustomerByIdExpectResult() {
        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
        CustomerDTO newCustomer = customerService.get(customer.getId());
        assertThat(newCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(newCustomer.getName()).isEqualTo(customer.getName());
        assertThat(newCustomer.getId()).isEqualTo(customer.getId());
        verify(customerRepository).findById(customer.getId());
    }

    @Test(expected = RecordNotFoundException.class)
    public void whenGettingCustomerByIdExpectNull() {
        Long id = 1L;
        when(customerRepository.getById(id)).thenReturn(null);
        customerService.get(id);
    }

    @Test
    public void whenGettingBooksExpectResult() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        List<CustomerDTO> newBooks = customerService.getCustomers();
        assertThat(newBooks.size()).isEqualTo(1);
        assertThat(newBooks.get(0).getId()).isEqualTo(customer.getId());
        assertThat(newBooks.get(0).getName()).isEqualTo(customer.getName());
        assertThat(newBooks.get(0).getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    public void whenGettingBooksExpectEmpty() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        List<CustomerDTO> newBooks = customerService.getCustomers();
        assertThat(newBooks).isEmpty();
    }

    @Test
    public void whenUpdateBookExpectResult() {
        when(customerRepository.existsById(any())).thenReturn(true);
        when(customerRepository.getById(any())).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);
        CustomerDTO result = customerService.update(customerDTO);
        assertThat(result.getEmail()).isEqualTo(customerDTO.getEmail());
        assertThat(result.getName()).isEqualTo(customerDTO.getName());
        assertThat(result.getId()).isEqualTo(customerDTO.getId());
    }

    @Test
    public void whenUpdateBookExpectNull() {
        when(customerRepository.existsById(any())).thenReturn(false);
        CustomerDTO result = customerService.update(customerDTO);
        assertThat(result).isNull();
    }

    @Test
    public void whenDeleteBookExpectResult() {
        customerService.delete(customer.getId());
        verify(customerRepository).deleteById(customer.getId());
    }
}
