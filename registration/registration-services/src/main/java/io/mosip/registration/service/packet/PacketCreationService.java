package io.mosip.registration.service.packet;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.advice.AuthenticationAdvice;
import io.mosip.registration.util.advice.PreAuthorizeUserId;

/**
 * Class for creating the Resident Registration as zip file
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface PacketCreationService {

	/**
	 * Creates the packet
	 * 
	 * @param registrationDTO
	 *            the enrollment data for which packet has to be created
	 * @throws RegBaseCheckedException
	 *             the checked exception
	 * @return the zip file as bytes
	 */
	byte[] create(RegistrationDTO registrationDTO) throws RegBaseCheckedException;
}
