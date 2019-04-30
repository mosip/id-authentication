package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

/**
 * Service class for User MachineMapping
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface UserMachineMappingService {
	/**
	 * global config details are synched from server to client
	 * 
	 * @return responseDto
	 */
	public ResponseDTO syncUserDetails();
	
	/**
	 * Fetch whether the user new to machine or not
	 * @param userId user ID
	 * @return response DTO
	 */
	public ResponseDTO isUserNewToMachine(String userId);
}
