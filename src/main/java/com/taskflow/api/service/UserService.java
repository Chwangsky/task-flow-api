package com.taskflow.api.service;

import com.taskflow.api.entity.UserEntity;

public interface UserService {
    UserEntity findByEmailAndIsOAuth(String email, boolean isOAuth);
}
