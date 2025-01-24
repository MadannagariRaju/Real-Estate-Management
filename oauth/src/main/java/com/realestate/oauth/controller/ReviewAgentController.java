package com.realestate.oauth.controller;

import com.realestate.oauth.entity.*;
import com.realestate.oauth.repository.BusinessProfileRepository;
import com.realestate.oauth.repository.MessageFromOwnerRepository;
import com.realestate.oauth.repository.ProfileRepository;
import com.realestate.oauth.repository.UserRepository;
import com.realestate.oauth.service.MessageFromOwnerService;
import com.realestate.oauth.service.ReviewAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ReviewAgentController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewAgentController.class);

    @Autowired
    private ReviewAgentService reviewAgentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageFromOwnerRepository messageFromOwnerRepository;

    @Autowired
    private BusinessProfileRepository businessProfileRepository;

    @PostMapping("/agent")
    public ResponseEntity<Map<String, Object>> addReview(@RequestBody ReviewAgent reviewAgent1, @RequestParam Long agentId, @AuthenticationPrincipal OidcUser principal) {
        logger.info("Adding review for agent ID: {}", agentId);

        String email = principal.getEmail();
        logger.debug("Authenticated user email: {}", email);

        Map<String, Object> response = new HashMap<>();

        try {
            // Save the review
            ReviewAgent reviewAgent = reviewAgentService.addReview(reviewAgent1, email, agentId);
            response.put("agentReview", reviewAgent);

            logger.info("Review added successfully for agent ID: {}", agentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding review for agent ID: {}", agentId, e);
            response.put("error", "Failed to add review.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/agent")
    public ResponseEntity<List<ReviewAgent>> getReviewsForAgent(@RequestParam Long agentId) {
        logger.info("Fetching reviews for agent ID: {}", agentId);

        try {
            List<ReviewAgent> reviews = reviewAgentService.getReviewsForAgent(agentId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            logger.error("Error fetching reviews for agent ID: {}", agentId, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/agent-reviews")
    public ResponseEntity<List<ReviewAgent>> getAgentReviews(@AuthenticationPrincipal OidcUser principal) {
        String email = principal.getEmail();
        logger.debug("Fetching reviews for user email: {}", email);

        try {
            List<ReviewAgent> reviews = reviewAgentService.getAgentReviews(email);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            logger.error("Error fetching reviews for user email: {}", email, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/get-message")
    public ResponseEntity<Map<String, Object>> getMessagesFromOwner(@AuthenticationPrincipal OidcUser principal) {
        String email = principal.getEmail();
        User user = userRepository.findByEmail(email);
        logger.info("Fetching messages for user email: {}", email);
        Map<String, Object> map = new HashMap<>();

        try {
            List<MessageFromOwner> messages = messageFromOwnerRepository.findByUserId(user.getId());
            logger.info("messages count: {}", messages.size());

            // Create a list to hold the agent details
            List<Map<String, Object>> contactedAgents = new ArrayList<>();

            // Loop through the messages and get the relevant agent details
            for (MessageFromOwner message : messages) {
                Integer agentId = Math.toIntExact(message.getAgentId());
                BusinessProfile agentProfile = businessProfileRepository.findByUserId(agentId);

                // Add agent details to the list
                Map<String, Object> agentDetails = new HashMap<>();
                agentDetails.put("agentName", agentProfile.getAgentName());
                agentDetails.put("bussinessName", agentProfile.getBusinessName());
                agentDetails.put("contact", agentProfile.getContact());
                agentDetails.put("email", agentProfile.getEmail());
//                agentDetails.put("message", message.getMessage());
                agentDetails.put("agentId", agentProfile.getUser().getId());

                // Add the agent details map to the list of contacted agents
                contactedAgents.add(agentDetails);
            }

            // Add the list to the response map
            map.put("contactedAgents", contactedAgents);

            return ResponseEntity.ok(map);
        } catch (Exception e) {
            logger.error("Error fetching messages for user email: {}", email, e);
            return ResponseEntity.status(500).build();
        }
    }

}
