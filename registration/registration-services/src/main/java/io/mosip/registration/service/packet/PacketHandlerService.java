package io.mosip.registration.service.packet;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;

/**
 * The class to handle the enrollment data and create packet out of it
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface PacketHandlerService {

	/**
	 * Method to create the packet data and encrypt the same
	 * 
	 * @param enrollmentDTO
	 * @return the {@link ResponseDTO} object
	 */
	public ResponseDTO handle(RegistrationDTO registrationDTO);
}
