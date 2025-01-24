package com.realestate.oauth.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_agent")
public class ReviewAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_description")
    private String reviewDescription;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "date_time")
    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "business_profile_id", referencedColumnName = "id", nullable = false)
    private BusinessProfile businessProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }

    public BusinessProfile getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(BusinessProfile businessProfile) {
        this.businessProfile = businessProfile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "ReviewAgent{" +
                "id=" + id +
                ", reviewDescription='" + reviewDescription + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", sentAt=" + sentAt +
                ", businessProfile=" + businessProfile +
                '}';
    }
}
