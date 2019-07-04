package io.mosip.registration.service.packet;

import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;

/**
 * Service interface to perform the virus scan in the configured locations
 * 
 * @author saravanakumar gnanaguru
 * @since 1.0.0
 */
public interface RegistrationPacketVirusScanService {

	/**
	 * Performs the virus scan in the configured locations
	 * 
	 * <p>
	 * The configured locations are as below:
	 * </p>
	 * <ul>
	 * <li>Registration Packets Store</li>
	 * <li>Pre-Registration Packets Store</li>
	 * <li>Application Logs</li>
	 * <li>Application Database</li>
	 * <li>Registration Client Application</li>
	 * </ul>
	 * 
	 * <p>
	 * Returns the status of the virus scan as {@link ResponseDTO} object.
	 * </p>
	 * 
	 * <p>
	 * If virus scan has completed successfully, {@link SuccessResponseDTO} will be
	 * set in {@link ResponseDTO} object
	 * </p>
	 * 
	 * <p>
	 * If any exception occurs, {@link ErrorResponseDTO} will be set in
	 * {@link ResponseDTO} object
	 * </p>
	 * 
	 * @return status of the virus scan as {@link ResponseDTO}
	 */
	ResponseDTO scanPacket();

}