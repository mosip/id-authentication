package io.mosip.registration.processor.manual.adjudication.service;

import org.springframework.stereotype.Service;
import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;

/**
 * Interface for Manual Verification Services
 * 
 * @author Pranav Kumar
 * @author Shuchita
 * @since 0.0.1
 *
 */
@Service
public interface ManualAdjudicationService {

	/**
	 * This method assigns earliest created Reg Id to a manual verification user
	 * 
	 * @param dto
	 *            The {@link UserDto} to whom a regId needs to be assigned
	 * 
	 * @return {@link ManualVerificationDTO}
	 */
	public ManualVerificationDTO assignApplicant(UserDto dto);

	/**
	 * This method returns a file related to a regId
	 * 
	 * @param regId
	 *            The registration ID
	 * @param fileName
	 *            The file required
	 * @return The file as bytes
	 */
	public byte[] getApplicantFile(String regId, String fileName);

	/**
	 * This method returns data related to a regId
	 * 
	 * @param regId
	 *            The registration ID
	 * @param fileName
	 *            The file required
	 * @return The file as bytes
	 */
	public byte[] getApplicantData(String regId, String fileName);

	/**
	 * This method updates the Manual Verification status of a regId according to
	 * decision taken by manual verifier
	 * 
	 * @param manualVerificationDTO
	 *            {@link ManualVerificationDTO}
	 * 
	 * @return The updated {@link ManualVerificationDTO}
	 */
	public ManualVerificationDTO updatePacketStatus(ManualVerificationDTO manualVerificationDTO);
}
