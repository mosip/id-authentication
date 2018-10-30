package io.mosip.preregistration.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.mosip.preregistration.application.dto.ResponseDto;
import io.mosip.preregistration.application.dto.ViewRegistrationResponseDto;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * Registration service interface
 * 
 * @author M1037717
 *
 */
@Service
public interface RegistrationService<T, U> {

	/**
	 * 
	 * @param registrationDto
	 * 
	 * @return responseDto
	 */

	public ResponseDto addRegistration(U registrationDto, String groupId) ;

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
	 * @param preregId
	 *            list
	 * 
	 * 
	 * @throws PrimaryMemberException
	 * 
	 * @returns List of response Dto
	 */
	public List<ResponseDto> deleteIndividual(String groupId, List<String> preregIds);

	/**
	 * @param groupId
	 * 
	 * 
	 */
	public List<ResponseDto> deleteGroup(String id);

}
