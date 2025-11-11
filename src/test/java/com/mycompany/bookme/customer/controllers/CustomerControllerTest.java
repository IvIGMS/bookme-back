package com.mycompany.bookme.customer.controllers;

import com.mycompany.bookme.customer.service.CustomerService;
import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.model.CustomerDTO;
import com.mycompany.bookme.model.CustomerRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @InjectMocks
    private CustomerController controller;

    @Mock
    private CustomerService service;

    @Test
    void createCustomer_returns201_andBody_fromService() {
        // given
        CustomerRequestDTO request = new CustomerRequestDTO();
        CustomerDTO expected = new CustomerDTO();

        when(service.createCustomer(request)).thenReturn(expected);

        // when
        ResponseEntity<CustomerDTO> response = controller.createCustomer(request);

        // then
        assertEquals(201, response.getStatusCodeValue());
        assertSame(expected, response.getBody());
        // Como se usa ResponseEntity.created(null), Location será null
        assertNull(response.getHeaders().getLocation());

        verify(service).createCustomer(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getCustomerById_returns200_andBody_fromService() {
        // given
        long id = 42L;
        CustomerDTO expected = new CustomerDTO();

        when(service.getCustomerById(id)).thenReturn(expected);

        // when
        ResponseEntity<CustomerDTO> response = controller.getCustomerById(id);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertSame(expected, response.getBody());

        verify(service).getCustomerById(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getCustomerById_propagates_NotFoundException() {
        // given
        long id = 99L;
        when(service.getCustomerById(id)).thenThrow(new NotFoundException("Customer not found: " + id));

        // when / then
        NotFoundException ex = assertThrows(NotFoundException.class, () -> controller.getCustomerById(id));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));

        verify(service).getCustomerById(id);
        verifyNoMoreInteractions(service);
    }
}
