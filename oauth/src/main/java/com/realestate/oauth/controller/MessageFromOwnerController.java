package com.realestate.oauth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.realestate.oauth.repository.MessageFromOwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import com.realestate.oauth.entity.MessageFromOwner;
import com.realestate.oauth.service.MessageFromOwnerService;

@RestController
@RequestMapping("/business")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MessageFromOwnerController {

	private static final Logger logger = LoggerFactory.getLogger(MessageFromOwnerController.class);

	@Autowired
	private MessageFromOwnerService messageFromOwnerService;

	@Autowired
	private MessageFromOwnerRepository messageFromOwnerRepository;

	@PostMapping("/post-messages")
	public ResponseEntity<Map<String, Object>> saveMessage(@RequestBody MessageFromOwner message, @AuthenticationPrincipal OAuth2User principal) {
		String email = principal.getAttribute("email");
		logger.info("Received post request from user: {}", email);

		if (message == null) {
			logger.warn("Message content is missing");
			return ResponseEntity.badRequest().body(Map.of("error", "Message content cannot be empty"));
		}

		try {
			MessageFromOwner savedMessage = messageFromOwnerService.saveMessage(message, email);
			Map<String, Object> response = new HashMap<>();
			response.put("post-message", savedMessage);
			logger.info("Message saved successfully for user: {}", email);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error occurred while saving message: {}", e.getMessage());
			return ResponseEntity.status(500).body(Map.of("error", "Failed to save message"));
		}
	}

	@GetMapping("/get-messages")
	public ResponseEntity<List<Map<String, Object>>> getMessages(@AuthenticationPrincipal OAuth2User principal) {
		String email = principal.getAttribute("email");
		logger.info("Fetching messages for user: {}", email);

		try {
			return messageFromOwnerService.getMessagesByAgentId(email);
		} catch (Exception e) {
			logger.error("Error occurred while fetching messages for user {}: {}", email, e.getMessage());
			return ResponseEntity.status(500).body(List.of(Map.of("error", "Failed to fetch messages")));
		}
	}

	@DeleteMapping("/delete-message/{id}")
	public ResponseEntity<Map<String, String>> deleteMessage(@PathVariable Long id) {
		logger.info("Request to delete message with ID: {}", id);

		try {
			messageFromOwnerService.deleteById(id);
			Map<String, String> response = new HashMap<>();
			response.put("status", "success");
			response.put("message", "Message deleted successfully.");
			logger.info("Message with ID {} deleted successfully", id);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error occurred while deleting message with ID {}: {}", id, e.getMessage());
			return ResponseEntity.status(500).body(Map.of("error", "Failed to delete message"));
		}
	}
}
