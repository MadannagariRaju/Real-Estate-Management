package com.realestate.oauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.oauth.entity.BusinessProfile;
import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.BusinessProfileRepository;
import com.realestate.oauth.repository.UserRepository;

@Service
public class BusinessProfileService {

	private static final Logger logger = LoggerFactory.getLogger(BusinessProfileService.class);

	@Autowired
	private BusinessProfileRepository businessProfileRepository;

	@Autowired
	private UserRepository userRepository;

	public BusinessProfile createProfile(BusinessProfile businessProfile, String email) {
		try {
			logger.info("Creating business profile for user with email: {}", email);

			User user = userRepository.findByEmail(email);
			if (user == null) {
				logger.error("User with email {} not found", email);
				throw new RuntimeException("User not found with email: " + email);
			}

			businessProfile.setUser(user);
			BusinessProfile profile = businessProfileRepository.save(businessProfile);
			logger.info("Business profile created successfully for user with email: {}", email);

			return profile;

		} catch (Exception e) {
			logger.error("Error creating business profile for user with email: {}", email, e);
			throw new RuntimeException("Error occurred while creating business profile for user: " + email, e);
		}
	}

	public BusinessProfile getProfile(Integer userId) {
		try {
			logger.info("Fetching business profile for user ID: {}", userId);
			return businessProfileRepository.findByUserId(userId);
		} catch (Exception e) {
			logger.error("Error fetching business profile for user ID: {}", userId, e);
			return null; // Returning null on failure
		}
	}

	public BusinessProfile getAgentProfile(Integer userId) {
		try {
			logger.info("Fetching agent profile for user ID: {}", userId);
			return businessProfileRepository.findByUserId(userId);
		} catch (Exception e) {
			logger.error("Error fetching agent profile for user ID: {}", userId, e);
			return null; // Returning null on failure
		}
	}
}
