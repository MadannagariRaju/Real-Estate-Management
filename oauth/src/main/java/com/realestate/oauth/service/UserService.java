package com.realestate.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // Check if the user exists by email
    public boolean isUserExists(String email) {
        boolean exists = userRepository.existsByEmail(email);
        logger.info("Checking if user exists with email: {}: {}", email, exists ? "Exists" : "Does not exist");
        return exists;
    }

    // Get role of the user by email
    public String getRole(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("No user found with email: {}", email);
            return null;
        }
        logger.info("Fetched role for user with email: {}: {}", email, user.getRole());
        return user.getRole();
    }

    // Save or update the user's role
    public void saveUserRole(String email, String role) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.info("No user found with email: {}. Creating new user with role: {}", email, role);
            user = new User(email, role); // Create a new user
        } else {
            logger.info("Updating role for user with email: {} to {}", email, role);
            user.setRole(role);
        }
        userRepository.save(user);
        logger.info("User role saved/updated for email: {}", email);
    }
}
