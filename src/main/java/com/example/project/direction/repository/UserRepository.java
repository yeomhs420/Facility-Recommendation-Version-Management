package com.example.project.direction.repository;

import com.example.project.direction.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUserId(String userId);

}
