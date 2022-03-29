package com.online.library.service;

import com.online.library.exception.RecordNotFoundException;
import com.online.library.model.Customer;
import com.online.library.model.dto.CustomerDTO;
import com.online.library.repository.CustomerRepository;
import com.online.library.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerDTO get(Long id) {
        log.debug("Finding customer by id {}!", id);
        return customerRepository.findById(id)
                .map(this::toCustomerDTO)
                .orElseThrow(() -> new RecordNotFoundException(
                        Constants.RECORD_NOT_FOUND_EXCEPTION));
    }

    private <U> CustomerDTO toCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer,customerDTO);
        return customerDTO;
    }

    public List<CustomerDTO> getCustomers() {
        log.debug("Getting all customers!");
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = new ArrayList();
        for(Customer customer : customers){
            CustomerDTO customerDTO = new CustomerDTO();
            BeanUtils.copyProperties(customer,customerDTO);
            customerDTOS.add(customerDTO);
        }
        return customerDTOS;
    }

    public CustomerDTO update(CustomerDTO customerDTO) {
        log.debug("Updating customer with id {}!", customerDTO.getId());
        if(customerRepository.existsById(customerDTO.getId())) {
            Customer customer = customerRepository.getById(customerDTO.getId());
            customer.setName(customerDTO.getName());
            customer.setEmail(customerDTO.getEmail());
            BeanUtils.copyProperties(customerRepository.save(customer),customerDTO);
            return customerDTO;
        }
        return null;
    }

    public void delete(Long id) {
        log.debug("Delete customer with id {}!", id);
        customerRepository.deleteById(id);
    }
}
