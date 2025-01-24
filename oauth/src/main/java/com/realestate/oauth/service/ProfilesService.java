package com.realestate.oauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.oauth.dto.ProfilesDto;
import com.realestate.oauth.entity.Profiles;
import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.ProfileRepository;
import com.realestate.oauth.repository.UserRepository;

import java.util.Optional;

@Service
public class ProfilesService {

	private static final Logger logger = LoggerFactory.getLogger(ProfilesService.class);

	private final ProfileRepository profileRepository;
	private final UserRepository userRepository;

	@Autowired
	public ProfilesService(ProfileRepository profileRepository , UserRepository userRepository) {
		this.profileRepository = profileRepository;
		this.userRepository = userRepository;
	}

	public Profiles createProfile(ProfilesDto profilesDto, String email) {
		try {
			logger.info("In createProfile method for email: {}", email);

			User user = userRepository.findByEmail(email);
			if (user == null) {
				logger.error("User not found for email: {}", email);
				throw new RuntimeException("User not found for email: " + email);
			}

			Optional<Profiles> existingProfile = profileRepository.findByUserId(user.getId());
			if (existingProfile.isPresent()) {
				Profiles profile = existingProfile.get();
				profile.setFullName(profilesDto.getFullName());
				profile.setPhno(profilesDto.getPhno());
				profile.setAddress(profilesDto.getAddress());

				logger.info("Updated profile for user: {}", email);

				return profileRepository.save(profile);
			}

			Profiles profiles = new Profiles();
			profiles.setEmail(email);
			profiles.setFullName(profilesDto.getFullName());
			profiles.setPhno(profilesDto.getPhno());
			profiles.setAddress(profilesDto.getAddress());
			profiles.setUser(user);

			logger.info("Created new profile for user: {}", email);

			return profileRepository.save(profiles);
		} catch (Exception e) {
			logger.error("Error creating profile for email: {}", email, e);
			throw new RuntimeException("Error creating profile: " + e.getMessage(), e);
		}
	}

	public Optional<Profiles> getProfile(Integer userId) {
		try {
			logger.info("Fetching profile for user ID: {}", userId);

			Optional<Profiles> profile = profileRepository.findByUserId(userId);

			if (profile.isPresent()) {
				logger.info("Profile found for user ID: {}", userId);
			} else {
				logger.warn("Profile not found for user ID: {}", userId);
			}

			return profile;
		} catch (Exception e) {
			logger.error("Error fetching profile for user ID: {}", userId, e);
			throw new RuntimeException("Error fetching profile: " + e.getMessage(), e);
		}
	}
}
