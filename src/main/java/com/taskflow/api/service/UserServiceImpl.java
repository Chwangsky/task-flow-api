package com.taskflow.api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.taskflow.api.entity.User;
import com.taskflow.api.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public Optional<User> findByEmailAndIsOAuth(String email, boolean isOAuth) {
        return userRepository.findByEmailAndIsOAuth(email, isOAuth);
    }
}
