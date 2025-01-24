package com.realestate.oauth.repository;

import com.realestate.oauth.entity.GeoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeoLocationRepository extends JpaRepository<GeoLocation,Long> {
    // Find properties based on the latitude and longitude in the GeoLocation entity
//    List<GeoLocation> findByLatitudeAndLongitude(Double latitude, Double longitude);

    @Query("SELECT g FROM GeoLocation g WHERE g.latitude BETWEEN :latMin AND :latMax AND g.longitude BETWEEN :lonMin AND :lonMax")
    List<GeoLocation> findByLatitudeBetweenAndLongitudeBetween(
            @Param("latMin") Double latMin,
            @Param("latMax") Double latMax,
            @Param("lonMin") Double lonMin,
            @Param("lonMax") Double lonMax);
}
