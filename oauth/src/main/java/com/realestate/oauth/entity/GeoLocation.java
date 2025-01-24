package com.realestate.oauth.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "geolocations")
public class GeoLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(precision = 30, scale = 20)
    private Double latitude;

//    @Column(precision = 30, scale = 20)
    private Double longitude;


    @OneToOne
    @JoinColumn(name = "property_id", referencedColumnName = "id", nullable = false, unique = true)
    private Property property; // Foreign key reference to Property table

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }


    public Long getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
