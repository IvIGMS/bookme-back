package com.mycompany.bookme.customer.service;

import com.mycompany.bookme.customer.dao.models.entities.CustomerEntity;
import com.mycompany.bookme.customer.dao.repositories.CustomerRepository;
import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.model.CustomerDTO;
import com.mycompany.bookme.model.CustomerRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createCustomer_mapsSavesAndReturnsDto() {
        // given
        CustomerRequestDTO request = new CustomerRequestDTO();
        CustomerEntity entityToSave = new CustomerEntity();
        CustomerEntity entitySaved = new CustomerEntity();
        CustomerDTO expectedDto = new CustomerDTO();

        when(modelMapper.map(request, CustomerEntity.class)).thenReturn(entityToSave);
        when(customerRepository.save(entityToSave)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, CustomerDTO.class)).thenReturn(expectedDto);

        // when
        CustomerDTO result = customerService.createCustomer(request);

        // then
        assertSame(expectedDto, result);
        verify(modelMapper).map(request, CustomerEntity.class);
        verify(customerRepository).save(entityToSave);
        verify(modelMapper).map(entitySaved, CustomerDTO.class);
        verifyNoMoreInteractions(modelMapper, customerRepository);
    }

    @Test
    void getCustomerById_returnsDto_whenFound() {
        // given
        long id = 1L;
        CustomerEntity entity = new CustomerEntity();
        CustomerDTO expectedDto = new CustomerDTO();

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, CustomerDTO.class)).thenReturn(expectedDto);

        // when
        CustomerDTO result = customerService.getCustomerById(id);

        // then
        assertSame(expectedDto, result);
        verify(customerRepository).findById(id);
        verify(modelMapper).map(entity, CustomerDTO.class);
        verifyNoMoreInteractions(modelMapper, customerRepository);
    }

    @Test
    void getCustomerById_throwsNotFound_whenMissing() {
        // given
        long id = 99L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> customerService.getCustomerById(id));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));

        verify(customerRepository).findById(id);
        verifyNoInteractions(modelMapper);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void getCustomerEntityById_returnsEntity_whenFound() {
        // given
        long id = 5L;
        CustomerEntity entity = new CustomerEntity();
        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));

        // when
        CustomerEntity result = customerService.getCustomerEntityById(id);

        // then
        assertSame(entity, result);
        verify(customerRepository).findById(id);
        verifyNoInteractions(modelMapper); // aquí no se usa el mapper
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void getCustomerEntityById_throwsNotFound_whenMissing() {
        // given
        long id = 123L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> customerService.getCustomerEntityById(id));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));

        verify(customerRepository).findById(id);
        verifyNoInteractions(modelMapper);
        verifyNoMoreInteractions(customerRepository);
    }
}
