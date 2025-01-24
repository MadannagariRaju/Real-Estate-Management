//package com.realestate.oauth.entity;
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "properties")
//public class Property {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//    private String description;
//    private String location;
//    private String propertyType; // "sell" or "rent"
//    private String price;
//    private String contact;
//    
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
//    @JsonManagedReference
//    private User user;  // This will establish the relationship with the User table
//    
//    // Getters and setters
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public String getPrice() {
//        return price;
//    }
//
//    public void setPrice(String price) {
//        this.price = price;
//    }
//
//    public String getContact() {
//        return contact;
//    }
//
//    public void setContact(String contact) {
//        this.contact = contact;
//    }
//
//    public String getPropertyType() {
//        return propertyType;
//    }
//
//    public void setPropertyType(String propertyType) {
//        this.propertyType = propertyType;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//}



































//
package com.realestate.oauth.entity;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    private String propertyType; // "sell" or "rent"
    private String price;
    private String contact;
//    private String amenties;
    private String role;

	private String amenities;

	private double latitude;
	private double longitude;


	@Lob
	@Basic(fetch = FetchType.EAGER)
    private byte[] imageData;
	
    @Lob
	@Basic(fetch = FetchType.EAGER)
    private byte[] videoData;

    @Lob
	@Basic(fetch = FetchType.EAGER)
    private byte[] floorPlanData;
    
    
    public byte[] getVideoData() {
		return videoData;
	}

	public void setVideoData(byte[] videoData) {
		this.videoData = videoData;
	}

	public byte[] getFloorPlanData() {
		return floorPlanData;
	}

	public void setFloorPlanData(byte[] floorPlanData) {
		this.floorPlanData = floorPlanData;
	}

	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

	@OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
	private GeoLocation geoLocation;

	@OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
	private Analytics analytics;

	@OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Message>message;


	// Getter and Setter
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
   

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String l) {
		this.price = l;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	
    public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Property{" +
				"id=" + id +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", location='" + location + '\'' +
				", propertyType='" + propertyType + '\'' +
				", price='" + price + '\'' +
				", contact='" + contact + '\'' +
				", role='" + role + '\'' +
				", amenities='" + amenities + '\'' +
				", imageData=" + Arrays.toString(imageData) +
				", videoData=" + Arrays.toString(videoData) +
				", floorPlanData=" + Arrays.toString(floorPlanData) +
				", user=" + user +
				'}';
	}
}
