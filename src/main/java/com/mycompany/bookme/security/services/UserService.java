package com.mycompany.bookme.security.services;

import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.model.RestaurantDTO;
import com.mycompany.bookme.model.RestaurantRequestDTO;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.dao.repositories.RestaurantRepository;
import com.mycompany.bookme.security.dao.models.entities.UserEntity;
import com.mycompany.bookme.security.dao.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserEntity getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }
}
