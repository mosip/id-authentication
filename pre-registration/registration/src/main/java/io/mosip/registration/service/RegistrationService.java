package io.mosip.registration.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.mosip.registration.core.exceptions.TablenotAccessibleException;
import io.mosip.registration.dto.ResponseDto;
import io.mosip.registration.dto.ViewRegistrationResponseDto;


@Service
public interface RegistrationService<T, U> {

	/**
	 * 
	 * @param registrationDto 
	 * 
	 * @return responseDto
	 */

	public ResponseDto addRegistration(U registrationDto, String groupId) throws TablenotAccessibleException ;
	
	/**
	 * @param userId
	 *            pass a userId through which user has logged in which can be either
	 *            email Id or phone number
	 * 
	 * @return List of groupIds
	 * @throws TableNotAccecibleException
	 */
	public List<ViewRegistrationResponseDto> getApplicationDetails(String userId) throws TablenotAccessibleException;

	/**
	 * @param groupId
	 *          
	 * 
	 * @return status of the application
	 * @throws TableNotAccecibleException
	 */
	public Map<String, String> getApplicationStatus(String groupId) throws TablenotAccessibleException;
	
	/**
	 * @param groupId 
	 * @param preregId list
	 *          
	 * 
	 * @throws PrimaryMemberException
	 */
	public void deleteIndividual(String groupId, List<String> preregIds);
	
	
	/**
	 * @param groupId 
	 *          
	 * 
	 */
	public void deleteGroup(String id);

}
