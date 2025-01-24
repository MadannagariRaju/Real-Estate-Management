package com.realestate.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.oauth.entity.BusinessProfile;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {

	 BusinessProfile findByUserId(Integer userId);
	
}
