package com.realestate.oauth.controller;

import com.realestate.oauth.entity.GeoLocation;
import com.realestate.oauth.repository.GeoLocationRepository;
import com.realestate.oauth.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/geolocation")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class GeoLocationController {

    private static final Logger logger = LoggerFactory.getLogger(GeoLocationController.class); // Logger for this class

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    @GetMapping("/get-properties-onpoints")
    public ResponseEntity<Map<String, Object>> getPropertiesByLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        logger.info("GeoLocation endpoint is hit. Latitude: {}, Longitude: {}", latitude, longitude);

        // Round to the first 3 decimal places (considering 0.001 tolerance)
        Double roundedLatitude = Math.round(latitude * 1000.0) / 1000.0;
        Double roundedLongitude = Math.round(longitude * 1000.0) / 1000.0;

        logger.info("Rounded Latitude: {}, Rounded Longitude: {}", roundedLatitude, roundedLongitude);

        try {
            // Tolerance for querying nearby properties
            Double tolerance = 0.001;

            // Find GeoLocations within the latitude and longitude tolerance range
            List<GeoLocation> geoLocations = geoLocationRepository.findByLatitudeBetweenAndLongitudeBetween(
                    roundedLatitude - tolerance, roundedLatitude + tolerance,
                    roundedLongitude - tolerance, roundedLongitude + tolerance);

            logger.info("GeoLocations found: {}", geoLocations.size());

            if (geoLocations.isEmpty()) {
                logger.warn("No geo locations found for the given coordinates: Latitude: {}, Longitude: {}", latitude, longitude);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No properties found for the given coordinates."));
            }

            // Fetch properties based on the found GeoLocations
            List<Object> properties = Collections.singletonList(geoLocations.stream()
                    .map(geoLocation -> propertyRepository.findById(geoLocation.getProperty().getId())
                            .orElse(null)) // Handle case where propertyId might not exist
                    .filter(Objects::nonNull) // Exclude null values
                    .toList());

            Map<String, Object> response = new HashMap<>();
            response.put("GeoLocationsFound", geoLocations);
            response.put("Properties", properties);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while fetching properties for Latitude: {}, Longitude: {}: {}", latitude, longitude, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while fetching properties."));
        }
    }

    @GetMapping("/get-all-geolocations")
    public ResponseEntity<List<GeoLocation>> getAllGeoLocations() {
        logger.info("Fetching all geolocations");

        try {
            List<GeoLocation> geoLocations = geoLocationRepository.findAll();
            logger.info("Fetched {} geolocations successfully", geoLocations.size());
            return ResponseEntity.ok(geoLocations);
        } catch (Exception e) {
            logger.error("An error occurred while fetching all geolocations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
