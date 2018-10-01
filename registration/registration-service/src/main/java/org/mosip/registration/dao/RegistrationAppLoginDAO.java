package org.mosip.registration.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationAppLoginDAO {

	public Map<String,Object> getModesOfLogin();
	
}
