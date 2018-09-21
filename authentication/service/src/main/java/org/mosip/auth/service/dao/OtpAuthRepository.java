package org.mosip.auth.service.dao;

import java.util.List;

import org.mosip.auth.service.entity.AuthRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpAuthRepository extends CrudRepository<AuthRequest, Integer> {

	public AuthRequest findByVirtualId(String virtualId);

	public AuthRequest findByUniqueId(String uniqueId);
	
	public List<AuthRequest> findAll();
} 