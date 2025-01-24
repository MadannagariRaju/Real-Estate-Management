package com.realestate.oauth.service;

import com.realestate.oauth.entity.Analytics;
import com.realestate.oauth.entity.Message;
import com.realestate.oauth.entity.Property;
import com.realestate.oauth.repository.AnalyticsRepository;
import com.realestate.oauth.repository.MessageRepository;
import com.realestate.oauth.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private MessageRepository messageRepository;

    public void incrementViewCount(Long propertyId) {
        try {
            logger.info("Inside increment view count service for property ID: {}", propertyId);

            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));

            List<Message> message = messageRepository.findByPropertyId(propertyId);

            Analytics analytics = analyticsRepository.findByPropertyId(propertyId)
                    .orElseGet(() -> {
                        Analytics newAnalytics = new Analytics();
                        newAnalytics.setProperty(property);
                        newAnalytics.setViewsCount(0L); // Initialize views count
                        newAnalytics.setInquriesCount((long) message.size()); // Initialize inquiries count
                        return newAnalytics;
                    });

            // Increment the view count
            analytics.setViewsCount(analytics.getViewsCount() + 1);
            analytics.setInquriesCount((long) message.size());

            // Save the analytics
            analyticsRepository.save(analytics);
            logger.info("View count incremented successfully for property ID: {}", propertyId);

        } catch (RuntimeException e) {
            logger.error("Error while incrementing view count for property ID: {}", propertyId, e);
            throw e; // Rethrow exception after logging
        } catch (Exception e) {
            logger.error("Unexpected error while processing increment view count for property ID: {}", propertyId, e);
            throw new RuntimeException("Unexpected error occurred while processing view count for property ID: " + propertyId, e);
        }
    }

    @Transactional
    public Optional<Analytics> getAnalyticsByPropertyId(Long propertyId) {
        try {
            logger.info("Fetching analytics for property ID: {}", propertyId);
            return analyticsRepository.findByPropertyId(propertyId);
        } catch (Exception e) {
            logger.error("Error fetching analytics for property ID: {}", propertyId, e);
            return Optional.empty(); // Return empty in case of failure
        }
    }
}
