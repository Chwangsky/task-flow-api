package com.taskflow.api.service;

import org.springframework.stereotype.Service;

import com.taskflow.api.entity.UserEntity;
import com.taskflow.api.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity findByEmailAndIsOAuth(String email, boolean isOAuth) {
        return userRepository.findByEmailAndIsOAuth(email, isOAuth)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with email: " + email + " and isOAuth: " + isOAuth));
    }
}
