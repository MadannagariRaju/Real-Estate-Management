package com.realestate.oauth.controller;

import com.realestate.oauth.dto.MessageDto;
import com.realestate.oauth.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import com.realestate.oauth.repository.UserRepository;
import com.realestate.oauth.service.MessageService;
import com.realestate.oauth.service.ProfilesService;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final ProfilesService profilesService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    public MessageController(MessageService messageService, UserRepository userRepository, ProfilesService profilesService) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.profilesService = profilesService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@AuthenticationPrincipal OAuth2User principal, @RequestBody MessageDto messageDto) {

        if (messageDto.getMessage() == null) {
            logger.warn("Message content is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Message content cannot be empty"));
        }

        String email = principal.getAttribute("email");
        logger.info("Sending message from user: {}", email);

        try {
            messageService.saveMessage(messageDto, email);
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageDto);
            logger.info("Message sent successfully from user: {}", email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred while sending message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to send message"));
        }
    }

    @GetMapping("/getmessage")
    public ResponseEntity<Map<String, Object>> getMessages(@AuthenticationPrincipal OAuth2User principal) {

        String email = principal.getAttribute("email");
        if (email == null) {
            logger.warn("Email not found in the request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email not found"));
        }

        try {
            Map<String, Object> messages = messageService.getMessages(email);
            if (messages == null || messages.isEmpty()) {
                logger.info("No messages found for user: {}", email);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "No messages found"));
            }

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            logger.error("Failed to fetch messages for user {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch messages"));
        }
    }

    @Transactional
    @GetMapping("/get-contacted-details")
    public ResponseEntity<Map<String, Object>> getContactedMessages(@AuthenticationPrincipal OAuth2User principal) {

        String email = principal.getAttribute("email");
        if (email == null) {
            logger.warn("Email not found in the request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email not found"));
        }

        try {
            Map<String, Object> contactedMessages = messageService.getContactedMessages(email).getBody();
            if (contactedMessages == null || contactedMessages.isEmpty()) {
                logger.info("No contacted messages found for user: {}", email);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "No messages found"));
            }

            return ResponseEntity.ok(contactedMessages);
        } catch (Exception e) {
            logger.error("Failed to fetch contacted messages for user {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch contacted messages"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {

        try {
            messageService.deleteMessageById(id);
            logger.info("Message with ID {} deleted successfully", id);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (EntityNotFoundException ex) {
            logger.error("Message with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404
        } catch (Exception e) {
            logger.error("Error occurred while deleting message with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500
        }
    }
}
