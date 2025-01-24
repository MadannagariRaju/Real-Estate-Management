package com.realestate.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.oauth.entity.Profiles;

public interface ProfileRepository extends JpaRepository<Profiles, Long> {
	Optional<Profiles> findByUserId(Integer userId);

	Optional<Profiles> findByEmail(String senderEmail);
}
