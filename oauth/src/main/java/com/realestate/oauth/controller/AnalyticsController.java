package com.realestate.oauth.controller;

import com.realestate.oauth.entity.Analytics;
import com.realestate.oauth.service.AnalyticsService;
import com.realestate.oauth.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AnalyticsController {

    // Initialize logger
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private PropertyService propertyService;

    @PutMapping("/incrementViews")
    public ResponseEntity<Map<String, String>> incrementViewCount(@RequestParam Long propertyId) {
        logger.info("Increment view count for property ID: {}", propertyId);
        Map<String, String> responseMap = new HashMap<>();

        try {
            analyticsService.incrementViewCount(propertyId);
            responseMap.put("view count", "increased");
            logger.info("View count for property ID {} successfully increased.", propertyId);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            logger.error("Error occurred while incrementing view count for property ID {}: {}", propertyId, e.getMessage());
            responseMap.put("error", e.getMessage());
            return ResponseEntity.status(500).body(responseMap); // Return HTTP 500 if error occurs
        }
    }

    @GetMapping("/getPropertyAnalytics/{propertyId}")
    public ResponseEntity<Optional<Analytics>> getPropertyAnalytics(@PathVariable Long propertyId) {
        logger.info("Fetching analytics for property ID: {}", propertyId);

        try {
            Optional<Analytics> analytics = analyticsService.getAnalyticsByPropertyId(propertyId);

            if (analytics.isPresent()) {
                logger.info("Analytics for property ID {} found.", propertyId);
                return ResponseEntity.ok(analytics);
            } else {
                logger.warn("No analytics found for property ID: {}", propertyId);
                return ResponseEntity.status(404).body(Optional.empty());
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching analytics for property ID {}: {}", propertyId, e.getMessage());
            return ResponseEntity.status(500).body(Optional.empty());
        }
    }
}
