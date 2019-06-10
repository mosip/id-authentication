package io.mosip.registration.processor.manual.verification.service;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.dto.UserDto;

/**
 * Interface for Manual Verification Services.
 *
 * @author Pranav Kumar
 * @author Shuchita
 * @since 0.0.1
 */
@Service
public interface ManualVerificationService {

	/**
	 * This method assigns earliest created Reg Id to a manual verification user.
	 *
	 * @param dto
	 *            The {@link UserDto} to whom a regId needs to be assigned
	 * @param string
	 * @return {@link ManualVerificationDTO}
	 */

	public ManualVerificationDTO assignApplicant(UserDto dto);

	/**
	 * This method returns a file related to a regId.
	 *
	 * @param regId
	 *            The registration ID
	 * @param fileName
	 *            The file required
	 * @return The file as bytes
	 * @throws java.io.IOException 
	 * @throws IOException 
	 * @throws ApisResourceAccessException 
	 * @throws PacketDecryptionFailureException 
	 */
	public byte[] getApplicantFile(String regId, String fileName) throws PacketDecryptionFailureException, ApisResourceAccessException, IOException, java.io.IOException;

	/**
	 * This method updates the Manual Verification status of a regId according to
	 * decision taken by manual verifier.
	 *
	 * @param manualVerificationDTO
	 *            {@link ManualVerificationDTO}
	 * @return The updated {@link ManualVerificationDTO}
	 */
	public ManualVerificationDTO updatePacketStatus(ManualVerificationDTO manualVerificationDTO, String stageName);

	/**
	 * Gets the applicant packet info.
	 *
	 * @param regId
	 *            the reg id
	 * @return the applicant packet info
	 * @throws java.io.IOException 
	 * @throws IOException 
	 * @throws ApisResourceAccessException 
	 * @throws PacketDecryptionFailureException 
	 */
	public PacketMetaInfo getApplicantPacketInfo(String regId) throws PacketDecryptionFailureException, ApisResourceAccessException, IOException, java.io.IOException;
}
