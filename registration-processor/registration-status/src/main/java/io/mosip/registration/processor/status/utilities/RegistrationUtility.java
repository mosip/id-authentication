package io.mosip.registration.processor.status.utilities;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
/**
 * 
 * @author M1048219
 *
 */
@Component
public class RegistrationUtility {
	
	@Autowired
	private SyncRegistrationDao syncRegistrationDao;
	public static String generateId() {
		return UUID.randomUUID().toString();
	}
	
	public boolean isPresent(String syncResgistrationId) {
		return syncRegistrationDao.findById(syncResgistrationId) != null;
	}

}
