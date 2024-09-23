package com.taskflow.api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.api.global.entity.UserGoogleTokenEntity;

@Repository
public interface UserGoogleTokenRepository extends JpaRepository<UserGoogleTokenEntity, Long> {

}