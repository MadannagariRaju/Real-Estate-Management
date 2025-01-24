package com.realestate.oauth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.oauth.entity.MessageFromOwner;

public interface MessageFromOwnerRepository extends JpaRepository<MessageFromOwner, Long> {

	MessageFromOwner save(MessageFromOwner message);
	
	List<MessageFromOwner> findByAgentId(Integer agentId);


    List<MessageFromOwner> findByUserId(Integer id);
}
