package com.realestate.oauth.repository;

import java.util.List;

import com.realestate.oauth.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.oauth.dto.MessageDto;
import com.realestate.oauth.entity.Message;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	Message save(MessageDto message);
	
	List<Message> findByUserId(Integer userId);

    List<Message> findByPropertyId(Long propertyId);

	List<Message> findByCurrentUserId(Integer userId);
}
