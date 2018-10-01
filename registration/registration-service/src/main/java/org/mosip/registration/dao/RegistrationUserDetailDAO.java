package org.mosip.registration.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationUserDetailDAO {
	
	public Map<String,String> getUserDetail(String userId);

}
