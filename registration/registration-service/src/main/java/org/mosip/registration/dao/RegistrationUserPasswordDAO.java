package org.mosip.registration.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationUserPasswordDAO {
	
	public String getPassword(String userId, String hashPassword);

}
