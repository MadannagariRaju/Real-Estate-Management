package com.realestate.oauth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import com.realestate.oauth.entity.User;
import com.realestate.oauth.repository.UserRepository;
import com.realestate.oauth.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/username")
    public Map<String,Object> getUserName(@AuthenticationPrincipal OAuth2User principal) {
        Map<String,Object> map = new HashMap<>();
        try {
            logger.info("Fetching username from controller");

            String name = principal.getAttribute("name");
            map.put("fullName", name);
        } catch (Exception e) {
            logger.error("Error fetching username", e);
            map.put("error", "Unable to fetch username");
        }
        return map;
    }

    @GetMapping("/success")
    public void success(@AuthenticationPrincipal OAuth2User principal,
                        HttpServletResponse response,
                        @AuthenticationPrincipal OidcUser oiduser,
                        HttpServletRequest request,
                        HttpSession session) throws IOException {

        try {
            String email = principal.getAttribute("email");
            String name = principal.getAttribute("name");

            logger.info("Requested Session ID: " + request.getRequestedSessionId());
            logger.info("Session ID: " + session.getId());

            logger.info("OIDC User: " + oiduser.getEmail() + " Name: " + oiduser.getAttribute("name"));

            if (userService.isUserExists(email)) {
                String role = userService.getRole(email);
                logger.info("User role: " + role);
                response.sendRedirect("http://localhost:4200/" + role + "-dashboard");
            } else {
                logger.info("User is being created...");
                response.sendRedirect("http://localhost:4200/select-role?email=" + email + "&name=" + name);
            }
        } catch (Exception e) {
            logger.error("Error during success processing", e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");
        }
    }

    @GetMapping("/get-role")
    public ResponseEntity<?> getRole(@AuthenticationPrincipal OAuth2User principal) {
        try {
            logger.info("Fetching role for user");

            String email = principal.getAttribute("email");
            String role = userService.getRole(email);
            logger.info("Role: " + role);

            return ResponseEntity.ok(role);
        } catch (Exception e) {
            logger.error("Error fetching user role", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching role");
        }
    }

    @PostMapping("/save-role")
    public ResponseEntity<String> saveRole(@AuthenticationPrincipal OAuth2User principal,
                                           @RequestBody Map<String, String> body,
                                           HttpServletResponse response) throws IOException {
        try {
            String email = body.get("email");
            String role = body.get("role");
            logger.info("Saving role: " + role + " for email: " + email);

            userService.saveUserRole(email, role);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            logger.error("Error saving role", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving role");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletRequest request, HttpSession session) {
        try {
            logger.info("Logout method triggered");

            logger.info("Before invalidation:");
            logger.info("Requested Session ID: " + request.getRequestedSessionId());
            logger.info("Session ID: " + session.getId());

            HttpSession newSession = request.getSession(false);
            if (newSession == null) {
                logger.info("No active session found after invalidation");
            } else {
                logger.info("New Session ID: " + newSession.getId());
            }

            // Invalidate the session
            if (session != null) {
                session.invalidate();
                logger.info("Session invalidated");
            }

            // Clear the SecurityContext
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getAuthStatus(HttpServletRequest request,
                                                             @AuthenticationPrincipal OAuth2User principal) {
        Map<String, String> response = new HashMap<>();

        try {
            logger.info("Checking authentication status...");

            boolean isAuthenticated = principal != null && !principal.getName().equals("anonymousUser");

            if (isAuthenticated && principal != null) {
                String email = principal.getAttribute("email");
                User user = userRepository.findByEmail(email);
                if (user != null) {
                    String role = user.getRole();
                    response.put("role", role);
                    response.put("isAuthenticated", "true");
                    logger.info("User Role: " + role);
                } else {
                    response.put("role", "unknown");
                    response.put("isAuthenticated", "false");
                    logger.warn("User not found: " + email);
                }
            } else {
                response.put("role", "unknown");
                response.put("isAuthenticated", "false");
            }
        } catch (Exception e) {
            logger.error("Error during authentication status check", e);
            response.put("error", "Error during authentication status check");
        }

        return ResponseEntity.ok(response);
    }
}
