package io.mosip.registration.processor.status.utilities;

import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * The Class RegistrationUtility.
 *
 * @author M1048219
 */
@Component
public class RegistrationUtility {

	/**
	 * Generate id.
	 *
	 * @return the string
	 */
	public static String generateId() {
		return UUID.randomUUID().toString();
	}

}
