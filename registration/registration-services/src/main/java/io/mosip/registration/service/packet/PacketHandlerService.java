package io.mosip.registration.service.packet;

import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;

/**
 * The interface to handle the registration data to create packet out of it and
 * save the encrypted packet data in the configured local system
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface PacketHandlerService {

	/**
	 * Creates the in-memory zip file (packet) out of the {@link RegistrationDTO}
	 * object, then encrypt the in-memory zip file and save the encrypted data in
	 * the local storage
	 * 
	 * <p>
	 * Returns the {@link ResponseDTO} object.
	 * </p>
	 * 
	 * <p>
	 * If all the above processes had completed successfully,
	 * {@link SuccessResponseDTO} will be set in {@link ResponseDTO} object
	 * </p>
	 * 
	 * <p>
	 * If any exception occurs, {@link ErrorResponseDTO} will be set in
	 * {@link ResponseDTO} object
	 * </p>
	 * 
	 * @param registrationDTO
	 *            the registration data out of which in-memory zip file has to be
	 *            created
	 * @return the {@link ResponseDTO} object
	 */
	public ResponseDTO handle(RegistrationDTO registrationDTO);
}
