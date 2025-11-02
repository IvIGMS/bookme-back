package com.mycompany.bookme.customer.service;

import com.mycompany.bookme.customer.dao.models.entities.CustomerEntity;
import com.mycompany.bookme.customer.dao.repositories.CustomerRepository;
import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.model.CustomerDTO;
import com.mycompany.bookme.model.CustomerRequestDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public CustomerDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        CustomerEntity customerEntityToBeSaved = modelMapper.map(customerRequestDTO, CustomerEntity.class);
        CustomerEntity customerEntitySaved = customerRepository.save(customerEntityToBeSaved);
        // todo: arreglar mapper para la auditoría
        return modelMapper.map(customerEntitySaved, CustomerDTO.class);
    }

    public CustomerDTO getCustomerById(Long customerId) {
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));
        return modelMapper.map(customerEntity, CustomerDTO.class);
    }

    public CustomerEntity getCustomerEntityById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));
    }
}
