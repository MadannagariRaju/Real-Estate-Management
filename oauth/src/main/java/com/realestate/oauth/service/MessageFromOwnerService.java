package com.realestate.oauth.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.realestate.oauth.entity.MessageFromOwner;
import com.realestate.oauth.entity.Profiles;
import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.MessageFromOwnerRepository;
import com.realestate.oauth.repository.ProfileRepository;
import com.realestate.oauth.repository.UserRepository;

@Service
public class MessageFromOwnerService {

	private static final Logger logger = LoggerFactory.getLogger(MessageFromOwnerService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MessageFromOwnerRepository messageFromOwnerRepository;

	@Autowired
	private ProfileRepository profileRepository;

	public MessageFromOwner saveMessage(MessageFromOwner message, String email) {
		try {
			User user = userRepository.findByEmail(email);
			if (user == null) {
				logger.error("User not found with email: {}", email);
				throw new RuntimeException("User not found with email: " + email);
			}

			Integer userId = user.getId();
			logger.info("User ID: {}", userId);

			message.setUser(user);
			message.setSentAt(LocalDateTime.now());
			messageFromOwnerRepository.save(message);

			logger.info("Message saved successfully for user: {}", userId);
			return message;
		} catch (Exception e) {
			logger.error("Error saving message for user: {}", email, e);
			throw new RuntimeException("Error saving message: " + e.getMessage(), e);
		}
	}

	public ResponseEntity<List<Map<String, Object>>> getMessagesByAgentId(String email) {
		logger.info("getMessagesByAgentId is hit for email: {}", email);

		try {
			User user = userRepository.findByEmail(email);
			if (user == null) {
				logger.error("User not found with email: {}", email);
				throw new RuntimeException("User not found with email: " + email);
			}

			Integer agentId = user.getId();
			logger.info("Agent ID: {}", agentId);

			List<Map<String, Object>> responseList = new ArrayList<>();

			List<MessageFromOwner> messages = messageFromOwnerRepository.findByAgentId(agentId);
			for (MessageFromOwner message : messages) {
				Map<String, Object> messageData = new HashMap<>();
				messageData.put("message", message);

				Profiles messageUser = profileRepository.findByUserId(message.getUser().getId()).orElse(null);
				messageData.put("userDetails", messageUser);

				responseList.add(messageData);
			}

			logger.info("Fetched {} messages for agent ID: {}", responseList.size(), agentId);
			return ResponseEntity.ok(responseList);
		} catch (Exception e) {
			logger.error("Error fetching messages for agent email: {}", email, e);
			throw new RuntimeException("Error fetching messages: " + e.getMessage(), e);
		}
	}

	public void deleteById(Long id) {
		try {
			messageFromOwnerRepository.deleteById(id);
			logger.info("Message deleted successfully for ID: {}", id);
		} catch (Exception e) {
			logger.error("Error deleting message with ID: {}", id, e);
			throw new RuntimeException("Error deleting message: " + e.getMessage(), e);
		}
	}
}
