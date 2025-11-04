package com.mycompany.bookme.customer.controllers;

import com.mycompany.bookme.api.CustomersApi;
import com.mycompany.bookme.customer.service.CustomerService;
import com.mycompany.bookme.exceptions.utils.ControllerUtils;
import com.mycompany.bookme.model.CustomerDTO;
import com.mycompany.bookme.model.CustomerRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CustomerController extends ControllerUtils implements CustomersApi {
    private final CustomerService customerService;

    @Override
    public ResponseEntity<CustomerDTO> createCustomer(CustomerRequestDTO customerRequestDTO) {
        return ResponseEntity.created(null).body(customerService.createCustomer(customerRequestDTO));
    }

    @Override
    public ResponseEntity<CustomerDTO> getCustomerById(Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }
}

