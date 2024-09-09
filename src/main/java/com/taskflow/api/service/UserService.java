package com.taskflow.api.service;

import java.util.Optional;

import com.taskflow.api.entity.User;

public interface UserService {
    Optional<User> findByEmailAndIsOAuth(String email, boolean isOAuth);
}
