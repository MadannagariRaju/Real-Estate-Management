package com.realestate.oauth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import com.realestate.oauth.entity.BusinessProfile;
import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.BusinessProfileRepository;
import com.realestate.oauth.repository.UserRepository;
import com.realestate.oauth.service.BusinessProfileService;

@RestController
@RequestMapping("/business")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BusinessProfileController {

    // Initialize logger
    private static final Logger logger = LoggerFactory.getLogger(BusinessProfileController.class);

    @Autowired
    private BusinessProfileService businessProfileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessProfileRepository businessProfileRepository;

    @PutMapping("/create-profile")
    public ResponseEntity<Map<String, Object>> createProfile(
            @RequestBody BusinessProfile businessProfile,
            @AuthenticationPrincipal OidcUser principal) {

        String email = principal.getEmail(); // Extract user email from principal
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Creating business profile for email: {}", email);
            BusinessProfile profile = businessProfileService.createProfile(businessProfile, email);
            response.put("status", "success");
            response.put("message", "Profile created successfully.");
            response.put("profile", profile);
            logger.info("Business profile created successfully for email: {}", email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating business profile for email: {}: {}", email, e.getMessage());
            response.put("status", "error");
            response.put("message", "Failed to create profile.");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal OidcUser principal) {

        String email = principal.getEmail();
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Fetching business profile for email: {}", email);
            User currentUser = userRepository.findByEmail(email);
            if (currentUser == null) {
                logger.warn("User not found with email: {}", email);
                response.put("status", "error");
                response.put("message", "User not found.");
                return ResponseEntity.status(404).body(response);
            }

            Integer userId = currentUser.getId();
            BusinessProfile profile = businessProfileService.getProfile(userId);
            if (profile == null) {
                logger.warn("Profile not found for userId: {}", userId);
                response.put("status", "error");
                response.put("message", "Profile not found.");
                return ResponseEntity.status(404).body(response);
            }

            response.put("status", "success");
            response.put("profile", profile);
            logger.info("Business profile fetched successfully for userId: {}", userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching business profile for email: {}: {}", email, e.getMessage());
            response.put("status", "error");
            response.put("message", "Failed to fetch profile.");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-all-business-profiles")
    public ResponseEntity<Map<String, Object>> getAllBusinessProfiles() {
        logger.info("Fetching all business profiles");

        Map<String, Object> responseMap = new HashMap<>();

        try {
            List<BusinessProfile> profiles = businessProfileRepository.findAll();
            responseMap.put("allBusinessProfiles", profiles); // Consistent key naming
            logger.info("Successfully fetched all business profiles, total count: {}", profiles.size());
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            logger.error("Error fetching all business profiles: {}", e.getMessage());
            responseMap.put("status", "error");
            responseMap.put("message", "Failed to fetch business profiles.");
            responseMap.put("error", e.getMessage());
            return ResponseEntity.status(500).body(responseMap);
        }
    }
}
