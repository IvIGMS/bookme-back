package com.mycompany.bookme.customer.dao.repositories;

import com.mycompany.bookme.customer.dao.models.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}