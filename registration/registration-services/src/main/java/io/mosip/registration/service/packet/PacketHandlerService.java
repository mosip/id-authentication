package io.mosip.registration.service.packet;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;

/**
 * The class to handle the registration data to create packet out of it and save
 * the registration packet data in {@link Registration}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface PacketHandlerService {

	/**
	 * Method to create the packet data and encrypt the same
	 * 
	 * @param registrationDTO the registration data
	 * @return the {@link ResponseDTO} object
	 */
	public ResponseDTO handle(RegistrationDTO registrationDTO);
}
