package com.realestate.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.oauth.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
	Long findById(Long userId1);
}

