package com.realestate.oauth.service;

import com.realestate.oauth.entity.GeoLocation;
import com.realestate.oauth.repository.GeoLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    public GeoLocation getCoordinates(Double lat, Double lng) {
        logger.info("Attempting to fetch coordinates for latitude: {} and longitude: {}", lat, lng);

        // Check if lat/lng are valid (non-zero)
        if (lat == 0 || lng == 0) {
            logger.error("Invalid coordinates received: latitude or longitude is zero.");
            throw new RuntimeException("Unable to fetch geolocation due to invalid coordinates.");
        }

        try {
            GeoLocation geoLocation = new GeoLocation();
            geoLocation.setLatitude(lat);
            geoLocation.setLongitude(lng);

            logger.info("Coordinates successfully fetched: Latitude - {} , Longitude - {}", lat, lng);

            return geoLocation;
        } catch (Exception e) {
            logger.error("Error while processing geolocation for latitude: {} and longitude: {}", lat, lng, e);
            throw new RuntimeException("Error while processing geolocation: " + e.getMessage(), e);
        }
    }
}
