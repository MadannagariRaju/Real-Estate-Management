package com.realestate.oauth.service;

import com.realestate.oauth.entity.Profiles;
import com.realestate.oauth.entity.ReviewAgent;
import com.realestate.oauth.entity.BusinessProfile;
import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.ProfileRepository;
import com.realestate.oauth.repository.ReviewAgentRepository;
import com.realestate.oauth.repository.BusinessProfileRepository;
import com.realestate.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewAgentService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewAgentService.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private BusinessProfileRepository businessProfileRepository;

    @Autowired
    private ReviewAgentRepository reviewAgentRepository;

    @Autowired
    private UserRepository userRepository;

    // Add a review for an agent
    public ReviewAgent addReview(ReviewAgent reviewAgent1, String email, Long agentId) {
        logger.info("Adding review for agent with ID: {}", agentId);

        Optional<Profiles> profile = profileRepository.findByEmail(email);
        if (!profile.isPresent()) {
            logger.error("Profile not found for email: {}", email);
            throw new IllegalArgumentException("Profile not found for email: " + email);
        }

        Optional<BusinessProfile> businessProfile = businessProfileRepository.findById(agentId);
        if (!businessProfile.isPresent()) {
            logger.error("Business profile not found for agent ID: {}", agentId);
            throw new IllegalArgumentException("Business profile not found for agent ID: " + agentId);
        }

        ReviewAgent reviewAgent = new ReviewAgent();
        reviewAgent.setReviewDescription(reviewAgent1.getReviewDescription());
        reviewAgent.setEmail(email);
        reviewAgent.setSentAt(LocalDateTime.now());
        reviewAgent.setName(profile.get().getFullName());
        reviewAgent.setBusinessProfile(businessProfile.get());

        reviewAgentRepository.save(reviewAgent);
        logger.info("Review added successfully for agent with ID: {}", agentId);

        return reviewAgent;
    }

    // Get reviews for a specific agent
    public List<ReviewAgent> getReviewsForAgent(Long agentId) {
        logger.info("Fetching reviews for agent with ID: {}", agentId);

        List<ReviewAgent> reviews = reviewAgentRepository.findByBusinessProfileId(agentId);
        if (reviews.isEmpty()) {
            logger.warn("No reviews found for agent with ID: {}", agentId);
        } else {
            logger.info("Found {} reviews for agent with ID: {}", reviews.size(), agentId);
        }

        return reviews;
    }

    // Get reviews for the agent associated with the given email
    public List<ReviewAgent> getAgentReviews(String email) {
        logger.info("Fetching reviews for agent with email: {}", email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found for email: {}", email);
            throw new IllegalArgumentException("User not found for email: " + email);
        }

        BusinessProfile businessProfile = businessProfileRepository.findByUserId(user.getId());
        if (businessProfile == null) {
            logger.error("Business profile not found for user with email: {}", email);
            throw new IllegalArgumentException("Business profile not found for user with email: " + email);
        }

        List<ReviewAgent> reviews = reviewAgentRepository.findByBusinessProfileId(businessProfile.getId());
        if (reviews.isEmpty()) {
            logger.warn("No reviews found for business profile with ID: {}", businessProfile.getId());
        } else {
            logger.info("Found {} reviews for business profile with ID: {}", reviews.size(), businessProfile.getId());
        }

        return reviews;
    }
}
