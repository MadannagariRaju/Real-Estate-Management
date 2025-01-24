package com.realestate.oauth.service;

import com.realestate.oauth.entity.*;
import com.realestate.oauth.repository.*;
import com.twilio.Twilio;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.realestate.oauth.dto.MessageDto;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, PropertyRepository propertyRepository , UserRepository userRepository, ProfileRepository profileRepository) {
        this.messageRepository = messageRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }


    // This saveMessage will work if the phno are verified in twilio website to send sms

//    public Message saveMessage(MessageDto messageDto, String email) {
//        final String ACCOUNT_SID = "yours_twilio_account_sid";
//        final String AUTH_TOKEN = "yours_twilio_auth_token";
//        try {
//            logger.info("In saveMessage method");
//
//            User currentUser = userRepository.findByEmail(email);
//            if (currentUser == null) {
//                logger.error("User not found with email: {}", email);
//                throw new EntityNotFoundException("User not found with email: " + email);
//            }
//
//            Long propId = messageDto.getPropertyId();
//            Optional<Property> optionalProperty = propertyRepository.findById(propId);
//
//            if (optionalProperty.isPresent()) {
//                Property propertyDetails = optionalProperty.get();
//                User user = propertyDetails.getUser();
//
//                Message message = new Message();
//                message.setProperty(propertyDetails);
//                message.setUser(user);
//                message.setMessageContent(messageDto.getMessage());
//                message.setEmail(email);
//                message.setSentAt(LocalDateTime.now());
//                message.setCurrentUserId(currentUser.getId());
//
//                logger.info("setting messages");
//                if(user.getRole().equals("customer")) {
//                    Optional<Profiles> profile = profileRepository.findByUserId(user.getId());
//                    Profiles profile1 = profile.get();
//                    String toPhoneNumber = Long.toString(profile1.getPhno());
//                    if (!toPhoneNumber.startsWith("+")) {
//                        toPhoneNumber = "+91" + toPhoneNumber; // Assuming India
//                    }
//
//                    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//                    com.twilio.rest.api.v2010.account.Message sms = com.twilio.rest.api.v2010.account.Message.creator(
//                            new com.twilio.type.PhoneNumber(toPhoneNumber),
//                            new com.twilio.type.PhoneNumber("+15076936420"),
//                            messageDto.getMessage()
//                    ).create();
//                }
//                if(user.getRole().equals("agent")){
//                    BusinessProfile bussinessProfile = bussinessProfileRepository.findByUserId(user.getId());
//                    String toPhoneNumber = bussinessProfile.getContact();
//                    if (!toPhoneNumber.startsWith("+")) {
//                        toPhoneNumber = "+91" + toPhoneNumber; // Assuming India
//                    }
//
//                    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//                    com.twilio.rest.api.v2010.account.Message sms = com.twilio.rest.api.v2010.account.Message.creator(
//                            new com.twilio.type.PhoneNumber(toPhoneNumber),
//                            new com.twilio.type.PhoneNumber("+15076936420"),
//                            messageDto.getMessage()
//                    ).create();
//                }
//
////                System.out.println(message.getSid());
//
//                logger.info("Message object: {}", message);
//
//                Optional<Analytics> optionalAnalytics = analyticsRepository.findByPropertyId(propertyDetails.getId());
//
//                if (optionalAnalytics.isPresent()) {
//                    Analytics analytics = optionalAnalytics.get();
//                    List<Message> messageCount = messageRepository.findByPropertyId(propertyDetails.getId());
//                    analytics.setInquriesCount((long) messageCount.size() + 1);
//
//                    analyticsRepository.save(analytics);
//                    logger.info("Analytics updated for property ID: {}", propertyDetails.getId());
//                } else {
//                    logger.warn("Analytics not found for property ID: {}", propertyDetails.getId());
//                }
//
//                return messageRepository.save(message);
//            } else {
//                logger.error("Property not found with ID: {}", propId);
//                throw new EntityNotFoundException("Property not found with ID: " + propId);
//            }
//        } catch (Exception e) {
//            logger.error("Error in saveMessage method", e);
//            throw new RuntimeException("Error saving message: " + e.getMessage(), e);
//        }
//    }


    public Message saveMessage(MessageDto messageDto, String email) {
        final String ACCOUNT_SID = "yours_twilio_account_sid";
        final String AUTH_TOKEN = "yours_twilio_auth_token";
        try {
            logger.info("In saveMessage method");

            User currentUser = userRepository.findByEmail(email);
            if (currentUser == null) {
                logger.error("User not found with email: {}", email);
                throw new EntityNotFoundException("User not found with email: " + email);
            }

            Long propId = messageDto.getPropertyId();
            Optional<Property> optionalProperty = propertyRepository.findById(propId);

            if (optionalProperty.isPresent()) {
                Property propertyDetails = optionalProperty.get();
                User user = propertyDetails.getUser();

                Message message = new Message();
                message.setProperty(propertyDetails);
                message.setUser(user);
                message.setMessageContent(messageDto.getMessage());
                message.setEmail(email);
                message.setSentAt(LocalDateTime.now());
                message.setCurrentUserId(currentUser.getId());

                logger.info("Message object: {}", message);

                Optional<Analytics> optionalAnalytics = analyticsRepository.findByPropertyId(propertyDetails.getId());

                if (optionalAnalytics.isPresent()) {
                    Analytics analytics = optionalAnalytics.get();
                    List<Message> messageCount = messageRepository.findByPropertyId(propertyDetails.getId());
                    analytics.setInquriesCount((long) messageCount.size() + 1);

                    analyticsRepository.save(analytics);
                    logger.info("Analytics updated for property ID: {}", propertyDetails.getId());
                } else {
                    logger.warn("Analytics not found for property ID: {}", propertyDetails.getId());
                }

                return messageRepository.save(message);
            } else {
                logger.error("Property not found with ID: {}", propId);
                throw new EntityNotFoundException("Property not found with ID: " + propId);
            }
        } catch (Exception e) {
            logger.error("Error in saveMessage method", e);
            throw new RuntimeException("Error saving message: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Map<String, Object> getMessages(String email) {
        try {
            logger.info("In getMessages method");

            User user = userRepository.findByEmail(email);
            if (user == null) {
                logger.error("User not found with email: {}", email);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            Integer userId = user.getId();
            logger.info("User ID: {}", userId);

            List<Message> messages = messageRepository.findByUserId(userId);
            logger.info("Fetched messages: {}", messages);

            Map<String, Object> map = new HashMap<>();
            map.put("User Email", email);

            List<Map<String, Object>> messageDetails = new ArrayList<>();
            for (Message message : messages) {
                Map<String, Object> messageMap = new HashMap<>();

                String senderEmail = message.getEmail();
                Optional<Profiles> profileOpt = profileRepository.findByEmail(senderEmail);
                if (!profileOpt.isPresent()) {
                    messageMap.put("Error", "Profile not found for email: " + senderEmail);
                } else {
                    Profiles profile = profileOpt.get();
                    messageMap.put("messageId", message.getId());
                    messageMap.put("Name", profile.getFullName());
                    messageMap.put("Email", senderEmail);
                    messageMap.put("Phone", profile.getPhno());
                    messageMap.put("Address", profile.getAddress());
                }

                messageMap.put("messageContent", message.getMessageContent());
                messageMap.put("sentAt", message.getSentAt());

                Optional<Property> propertyDetails = propertyRepository.findById(message.getProperty().getId());
                if (propertyDetails.isPresent()) {
                    Property property = propertyDetails.get();
                    messageMap.put("propertyTitle", property.getTitle());
                    messageMap.put("propertyDescription", property.getDescription());
                    messageMap.put("propertyType", property.getPropertyType());
                    messageMap.put("prop_id", property.getId());
                } else {
                    messageMap.put("Error", "Property not found for ID: " + message.getProperty().getId());
                }

                messageDetails.add(messageMap);
            }

            map.put("Messages", messageDetails);
            return map;
        } catch (Exception e) {
            logger.error("Error fetching messages for email: {}", email, e);
            throw new RuntimeException("Error fetching messages: " + e.getMessage(), e);
        }
    }

    public boolean deleteMessageById(Long id) {
        try {
            if (messageRepository.existsById(id)) {
                messageRepository.deleteById(id);
                logger.info("Message with ID: {} deleted successfully", id);
                return true;
            } else {
                logger.warn("Message with ID: {} not found for deletion", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error deleting message with ID: {}", id, e);
            throw new RuntimeException("Error deleting message: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Map<String, Object>> getContactedMessages(String email) {
        try {
            logger.info("In getContactedMessages method");

            User user = userRepository.findByEmail(email);
            if (user == null) {
                logger.error("User not found with email: {}", email);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            Integer userId = user.getId();
            logger.info("User ID: {}", userId);

            List<Message> messages = messageRepository.findByCurrentUserId(userId);
            Map<String, Object> response = new HashMap<>();

            if (messages.isEmpty()) {
                response.put("message", "No messages found for this user");
                return ResponseEntity.ok(response);
            }

            Set<Long> set = new HashSet<>();
            List<Map<String, Object>> properties = new ArrayList<>();
            for (Message message : messages) {
                Property property = message.getProperty();
                if (!set.contains(property.getId())) {
                    Map<String, Object> propertyDetails = new HashMap<>();
                    propertyDetails.put("propertyId", property.getId());
                    propertyDetails.put("propertyTitle", property.getTitle());
                    propertyDetails.put("propertyType", property.getPropertyType());
                    propertyDetails.put("propertyPrice", property.getPrice());
                    propertyDetails.put("propertyContact", property.getContact());
                    properties.add(propertyDetails);
                }
                set.add(property.getId());
            }

            response.put("properties", properties);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching contacted messages for email: {}", email, e);
            throw new RuntimeException("Error fetching contacted messages: " + e.getMessage(), e);
        }
    }
}
