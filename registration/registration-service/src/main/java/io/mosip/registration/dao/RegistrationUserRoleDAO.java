package io.mosip.registration.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationUserRoleDAO {
	
	public List<String> getRoles(String userId);
	
}
