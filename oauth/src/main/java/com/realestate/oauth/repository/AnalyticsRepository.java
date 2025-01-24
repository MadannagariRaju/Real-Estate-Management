package com.realestate.oauth.repository;

import com.realestate.oauth.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics,Long> {

    Optional<Analytics> findByPropertyId(Long propertyId);
}
