package com.mycompany.bookme.security.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.security.dao.models.entities.UserEntity;
import com.mycompany.bookme.security.dao.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

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
