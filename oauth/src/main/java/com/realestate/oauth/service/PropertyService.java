package com.realestate.oauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import com.realestate.oauth.entity.Property;
import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.PropertyRepository;
import com.realestate.oauth.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Property addProperty(Property propertyGot, String email) {
        logger.info("Adding property for user with email: {}", email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        Property property = new Property();
        property.setTitle(propertyGot.getTitle());
        property.setDescription(propertyGot.getDescription());
        property.setLocation(propertyGot.getLocation());
        property.setPropertyType(propertyGot.getPropertyType());
        property.setPrice(propertyGot.getPrice());
        property.setContact(propertyGot.getContact());
        property.setRole(user.getRole());
        property.setAmenities(propertyGot.getAmenities());

        if (propertyGot.getImageData() != null) {
            property.setImageData(propertyGot.getImageData());
        }
        if (propertyGot.getVideoData() != null) {
            property.setVideoData(propertyGot.getVideoData());
        }
        if (propertyGot.getFloorPlanData() != null) {
            property.setFloorPlanData(propertyGot.getFloorPlanData());
        }

        property.setUser(user);

        logger.info("Property successfully added for user: {}", email);

        return propertyRepository.save(property);
    }

    @Transactional
    public List<Property> getPropertiesByUserId(Integer userId) {
        logger.info("Fetching properties for user with ID: {}", userId);

        List<Property> properties = propertyRepository.findByUserId(userId);

        if (properties.isEmpty()) {
            logger.warn("No properties found for user with ID: {}", userId);
        } else {
            logger.info("Found {} properties for user with ID: {}", properties.size(), userId);
        }

        return properties;
    }

    public void deleteProperty(Long propertyId) {
        logger.info("Attempting to delete property with ID: {}", propertyId);

        Optional<Property> property = propertyRepository.findById(propertyId);

        if (property.isPresent()) {
            propertyRepository.deleteById(propertyId);
            logger.info("Property with ID: {} successfully deleted", propertyId);
        } else {
            logger.error("Property not found with ID: {}", propertyId);
            throw new EntityNotFoundException("Property not found with ID: " + propertyId);
        }
    }
}
