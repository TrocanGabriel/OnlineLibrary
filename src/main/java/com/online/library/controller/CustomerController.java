package com.online.library.controller;

import com.online.library.exception.RecordNotFoundException;
import com.online.library.model.dto.CustomerDTO;
import com.online.library.service.CustomerService;
import com.online.library.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> get(@PathVariable("customerId") Long customerId){

        log.info("Get customerDTO with id {}", customerId);

        CustomerDTO customerDTO = customerService.get(customerId);
        if(customerDTO != null) {
            log.info("CustomerDTO details retrieved successful for customerDTO id {}", customerId);
            return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
        } else {
            log.info("CustomerDTO not found with id {}", customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "")
    public ResponseEntity<List<CustomerDTO>> getCustomers() {
        log.info("Get customers list!");

        List<CustomerDTO> customers;
        customers = customerService.getCustomers();

        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> update(@RequestBody CustomerDTO customerDTO, @PathVariable("customerId") Long customerId) {
        log.info("CustomerDTO with id {} is being updated", customerId);

        customerDTO.setId(customerId);
        CustomerDTO updatedCustomer = customerService.update(customerDTO);

        if(updatedCustomer == null){
            log.info("CustomerDTO with id {} was not found!", customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!updatedCustomer.getId().equals(customerId)) {
            log.info("CustomerDTO with id {} already contains updated data!", updatedCustomer.getId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(updatedCustomer);
        } else {
            log.info("CustomerDTO with id {} updated successfully!", updatedCustomer.getId());
            return ResponseEntity.status(HttpStatus.OK).body(updatedCustomer);
        }
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable("customerId") Long customerId) {
        log.info("Delete customerDTO with id {}!", customerId);

        if(customerService.get(customerId) == null) {
            throw new RecordNotFoundException(Constants.RECORD_NOT_FOUND_EXCEPTION);
        }
        customerService.delete(customerId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
