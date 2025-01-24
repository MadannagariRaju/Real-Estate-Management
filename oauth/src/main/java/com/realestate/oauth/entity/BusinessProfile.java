package com.realestate.oauth.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "business_profile")
public class BusinessProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false)
    private String businessName;

	@Column(name = "agent_name", nullable = false)
    private String agentName;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "specializations", nullable = true)
    private String specializations;

    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Column(name = "working_hours", nullable = false)
    private String workingHours;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(name = "experience", nullable = false)
    private int experience;
    
    @Column(nullable = false)
    private String email="abc@gmail.com";

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "businessProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewAgent> reviewAgents;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecializations() {
        return specializations;
    }

    public void setSpecializations(String specializations) {
        this.specializations = specializations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
    
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	@Override
	public String toString() {
		return "BusinessProfile [id=" + id + ", businessName=" + businessName + ", agentName=" + agentName
				+ ", contact=" + contact + ", location=" + location + ", specializations=" + specializations
				+ ", description=" + description + ", workingHours=" + workingHours + ", licenseNumber=" + licenseNumber
				+ ", experience=" + experience + ", user=" + user + "]";
	}
    
    
}
