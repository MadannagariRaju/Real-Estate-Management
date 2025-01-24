package com.realestate.oauth.controller;

import com.realestate.oauth.entity.GeoLocation;
import com.realestate.oauth.repository.GeoLocationRepository;
import com.realestate.oauth.service.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.oauth.entity.Property;
import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.PropertyRepository;
import com.realestate.oauth.repository.UserRepository;
import com.realestate.oauth.service.PropertyService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/prop")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyService propertyService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @Autowired
    public PropertyController(PropertyRepository propertyRepository, UserRepository userRepository ,PropertyService propertyService ) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.propertyService = propertyService;
    }

    @PostMapping("/addproperties")
    public ResponseEntity<Property> addProperty(
            @AuthenticationPrincipal OidcUser principal,
            @RequestPart("propertyDto") Property propertyDto,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "video", required = false) MultipartFile video,
            @RequestPart(value = "floorPlan", required = false) MultipartFile floorPlan,
            HttpServletRequest request,
            HttpSession session) throws IOException {

        logger.info("Inside PropertyController: addProperty");

        if (principal == null) {
            logger.error("Unauthorized access attempt - no principal found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = principal.getEmail();

        if(propertyDto.getTitle() == null || propertyDto.getPropertyType() == null || propertyDto.getAmenities() == null || propertyDto.getContact() == null || propertyDto.getDescription() == null){
            logger.error("Bad request: Missing required fields.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            // Handle image file
            if (image != null && !image.isEmpty()) {
                propertyDto.setImageData(image.getBytes());
            }

            // Handle video file
            if (video != null && !video.isEmpty()) {
                propertyDto.setVideoData(video.getBytes());
            }

            // Handle floor plan file
            if (floorPlan != null && !floorPlan.isEmpty()) {
                propertyDto.setFloorPlanData(floorPlan.getBytes());
            }

            // Fetch geolocation for the given location name
            GeoLocation geoLocation = geocodingService.getCoordinates(propertyDto.getLatitude(), propertyDto.getLongitude());

            if (geoLocation == null) {
                logger.error("Bad request: Geolocation not found.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // Save the property
            Property savedProperty = propertyService.addProperty(propertyDto, email);

            // Save the GeoLocation entity
            geoLocation.setProperty(savedProperty);
            geoLocationRepository.save(geoLocation);

            logger.info("Property added successfully with ID: {}", savedProperty.getId());

            return ResponseEntity.ok(savedProperty);

        } catch (Exception e) {
            logger.error("Error while adding property: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getproperties")
    public List<Map<String, Object>> getAllProperties(@AuthenticationPrincipal OidcUser principal) {
        logger.info("Fetching all properties for the current user.");

        try {
            // Validate user
            String email = principal.getEmail(); // Get the email from the OIDC user
            User currentUser = userRepository.findByEmail(email); // Find the user by email
            if (currentUser == null) {
                logger.warn("User not found with email: {}", email);
                return Collections.emptyList(); // Return empty list if user not found
            }

            // Get user ID and fetch properties
            Integer userId = currentUser.getId();
            List<Property> propertyList = propertyService.getPropertiesByUserId(userId);

            // Build the response with URLs for image, video, and floor plan
            return propertyList.stream().map(property -> {
                Map<String, Object> propertyResponse = new HashMap<>();
                propertyResponse.put("id", property.getId());
                propertyResponse.put("title", property.getTitle());
                propertyResponse.put("description", property.getDescription());
                propertyResponse.put("location", property.getLocation());
                propertyResponse.put("propertyType", property.getPropertyType());
                propertyResponse.put("price", property.getPrice());
                propertyResponse.put("amenities",property.getAmenities());
                propertyResponse.put("contact", property.getContact());
                propertyResponse.put("imageUrl", "http://localhost:8080/prop/getPropertyImage/" + property.getId());
                propertyResponse.put("videoUrl", "http://localhost:8080/prop/getPropertyVideo/" + property.getId());
                propertyResponse.put("floorPlanUrl", "http://localhost:8080/prop/getPropertyFloorPlan/" + property.getId());
                propertyResponse.put("role", property.getRole());
                return propertyResponse;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error while fetching properties: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @GetMapping("/getPropertyImage/{propertyId}")
    public ResponseEntity<byte[]> getPropertyImage(@PathVariable Long propertyId) {
        logger.info("Fetching property image for property ID: {}", propertyId);
        return getPropertyMedia(propertyId, "image");
    }

    @GetMapping("/getPropertyVideo/{propertyId}")
    public ResponseEntity<byte[]> getPropertyVideo(@PathVariable Long propertyId) {
        logger.info("Fetching property video for property ID: {}", propertyId);
        return getPropertyMedia(propertyId, "video");
    }

    @GetMapping("/getPropertyFloorPlan/{propertyId}")
    public ResponseEntity<byte[]> getPropertyFloorPlan(@PathVariable Long propertyId) {
        logger.info("Fetching property floor plan for property ID: {}", propertyId);
        return getPropertyMedia(propertyId, "floorPlan");
    }

    private ResponseEntity<byte[]> getPropertyMedia(Long propertyId, String mediaType) {
        try {
            Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new EntityNotFoundException("Property not found"));

            byte[] mediaData = null;

            switch (mediaType) {
                case "image":
                    mediaData = property.getImageData();
                    break;
                case "video":
                    mediaData = property.getVideoData();
                    break;
                case "floorPlan":
                    mediaData = property.getFloorPlanData();
                    break;
            }

            if (mediaData == null) {
                logger.error("No media data found for property ID: {}", propertyId);
                return ResponseEntity.notFound().build();
            }

            logger.info("Successfully fetched {} for property ID: {}", mediaType, propertyId);
            return ResponseEntity.ok().body(mediaData);
        } catch (Exception e) {
            logger.error("Error while fetching property media for ID {}: {}", propertyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteproperty/{id}")
    public ResponseEntity<Map<String,String>> deleteProperty(@PathVariable("id") Long propertyId) {
        logger.info("Attempting to delete property with ID: {}", propertyId);
        Map<String,String> res = new HashMap<>();

        try {
            propertyService.deleteProperty(propertyId);
            res.put("message", "deleted successfully");
            logger.info("Property with ID: {} deleted successfully.", propertyId);
            return ResponseEntity.ok(res);

        } catch (EntityNotFoundException ex) {
            logger.error("Property with ID: {} not found.", propertyId);
            res.put("message", "property not found");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            logger.error("Error while deleting property: {}", e.getMessage());
            res.put("message", "error occurred while deleting");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @GetMapping("/getallproperties")
    public List<Map<String, Object>> getAllProperties() {
        logger.info("Fetching all properties.");

        try {
            List<Property> propertyList = propertyRepository.findAll();
            return propertyList.stream().map(property -> {
                Map<String, Object> propertyResponse = new HashMap<>();
                propertyResponse.put("id", property.getId());
                propertyResponse.put("title", property.getTitle());
                propertyResponse.put("description", property.getDescription());
                propertyResponse.put("location", property.getLocation());
                propertyResponse.put("propertyType", property.getPropertyType());
                propertyResponse.put("price", property.getPrice());
                propertyResponse.put("amenities", property.getAmenities());
                propertyResponse.put("contact", property.getContact());
                propertyResponse.put("imageUrl", "http://localhost:8080/prop/getPropertyImage/" + property.getId());
                propertyResponse.put("videoUrl", "http://localhost:8080/prop/getPropertyVideo/" + property.getId());
                propertyResponse.put("floorPlanUrl", "http://localhost:8080/prop/getPropertyFloorPlan/" + property.getId());
                propertyResponse.put("role", property.getRole());
                return propertyResponse;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while fetching all properties: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
