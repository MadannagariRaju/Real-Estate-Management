package com.realestate.oauth.controller;

import com.realestate.oauth.entity.*;
import com.realestate.oauth.service.BusinessProfileService;
import com.realestate.oauth.service.MessageFromOwnerService;
import com.realestate.oauth.service.ProfilesService;
import com.realestate.oauth.dto.ProfilesDto;
import com.realestate.oauth.repository.PropertyRepository;
import com.realestate.oauth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ProfilesService profilesService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private MessageFromOwnerService messageFromOwnerService;

    @Autowired
    private BusinessProfileService businessProfileService;

    @PutMapping("/create")
    public ResponseEntity<Profiles> createProfile(@RequestBody ProfilesDto profilesDto, @AuthenticationPrincipal OidcUser principal) {
        String email = principal.getEmail();
        logger.info("Creating profile for user: {}", email);

        if (profilesDto == null) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            Profiles profile = profilesService.createProfile(profilesDto, email);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("Error creating profile for user {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getprofile")
    public ResponseEntity<Optional<Profiles>> getProfile(@AuthenticationPrincipal OidcUser principal) {
        String email = principal.getEmail();
        logger.info("Fetching profile for user: {}", email);

        User currentUser = userRepository.findByEmail(email);
        if (currentUser == null) {
            logger.warn("User not found: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
        }

        Integer userId = currentUser.getId();
        logger.info("User ID for email {}: {}", email, userId);

        Optional<Profiles> profile = profilesService.getProfile(userId);
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
        }
    }

    @GetMapping("/getownerbyproperty/{propertyId}")
    public ResponseEntity<Map<String, Object>> getOwnerByProperty(@PathVariable Long propertyId) {
        logger.info("Fetching owner details for property ID: {}", propertyId);

        Optional<Property> property = propertyRepository.findById(propertyId);
        Map<String, Object> response = new HashMap<>();

        if (property.isPresent()) {
            User owner = property.get().getUser();
            Integer userId = owner.getId();

            if ("owner".equals(owner.getRole())) {
                Optional<Profiles> profile = profilesService.getProfile(userId);
                if (profile.isPresent()) {
                    response.put("ownerDetails", profile.get());
                    response.put("role", "owner");
                    return ResponseEntity.ok(response);
                }
            }

            if ("agent".equals(owner.getRole())) {
                BusinessProfile businessProfile = businessProfileService.getAgentProfile(userId);
                if (businessProfile != null) {
                    response.put("name", businessProfile.getAgentName());
                    response.put("businessName", businessProfile.getBusinessName());
                    response.put("email", businessProfile.getEmail());
                    response.put("phno", businessProfile.getContact());
                    response.put("role", "agent");
                    return ResponseEntity.ok(response);
                }
            }
        }

        logger.warn("Property not found with ID: {}", propertyId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
