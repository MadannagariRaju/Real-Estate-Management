package com.realestate.oauth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.oauth.entity.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // This method will fetch all properties associated with a user by the user's id
    List<Property> findByUserId(Integer userId);
    void deleteById(Long propertyId);

    String findEmailById(Long propId);
}
