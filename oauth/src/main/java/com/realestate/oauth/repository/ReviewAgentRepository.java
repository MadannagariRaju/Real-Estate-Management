package com.realestate.oauth.repository;


import com.realestate.oauth.entity.ReviewAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewAgentRepository extends JpaRepository<ReviewAgent,Long> {

    List<ReviewAgent> findByBusinessProfileId(Long agentId);

//    List<ReviewAgent> findByAgentName(String agentName);
}
