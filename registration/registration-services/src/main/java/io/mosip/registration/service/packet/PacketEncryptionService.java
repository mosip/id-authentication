package io.mosip.registration.service.packet;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This interface encrypts the Registration packet using AES and RSA algorithms.
 * Then saves the encrypted packet in the specified location. And adds an entry
 * in the {@link Registration} table
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface PacketEncryptionService {

	/**
	 * Encrypts the input data using AES algorithm followed by RSA and stores the
	 * encrypted data in the specified location.
	 * 
	 * <p>
	 * This method does the following:
	 * </p>
	 * <ol>
	 * <li>Encrypts the input data using AES algorithm followed by RSA
	 * algorithm</li>
	 * <li>Validates the size of the encrypted data against the configured size. If
	 * exceeds the configured size, information will be logged in the logger</li>
	 * <li>Saves the encrypted data in the local system</li>
	 * <li>Save the registration details in the {@link Registration} table</li>
	 * <li>Save the details of audits attached with the packet against the
	 * registration ID in the {@link AuditLogControl} table</li>
	 * </ol>
	 * 
	 * <p>
	 * Returns the {@link ResponseDTO} object.
	 * 
	 * If all the above processes had completed successfully,
	 * {@link SuccessResponseDTO} will be set in {@link ResponseDTO} object
	 * </p>
	 * 
	 * @param registrationDTO
	 *            the {@link RegistrationDTO} containing the registration details
	 * @param packetZipData
	 *            the data to be encrypted
	 * @return the {@link ResponseDTO}
	 * @throws RegBaseCheckedException
	 *             the checked exception
	 */
	ResponseDTO encrypt(final RegistrationDTO registrationDTO, final byte[] packetZipData)
			throws RegBaseCheckedException;
}
