package io.mosip.kernel.masterdata.service;

import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.dto.RegCenterMachineUserReqDto;
import io.mosip.kernel.masterdata.dto.RegCenterMachineUserResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;

/**
 * Service for user machine mapping
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 * @see RegistrationCenterMachineUserID
 * @see RegistrationCenterUserMachineMappingDto
 */
@Service
public interface RegistrationCenterMachineUserService {

	/**
	 * Create a mapping of registration center,user,and machine
	 * 
	 * @param registrationCenterUserMachineMappingDto
	 *            {@link RegistrationCenterUserMachineMappingDto} request
	 * @return {@link RegistrationCenterMachineUserID}
	 */
	RegistrationCenterMachineUserID createRegistrationCentersMachineUserMapping(
			RequestDto<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDto);

	/**
	 * Delete a mapping of registration center, user and machine
	 * 
	 * @param regCenterId
	 *            input from user
	 * @param machineId
	 *            input from user
	 * @param userId
	 *            input from user
	 * @return {@link RegistrationCenterMachineUserID}
	 * @throws RequestException
	 *             when data not found
	 * @throws MasterDataServiceException
	 *             when data not properly deleted
	 */
	RegistrationCenterMachineUserID deleteRegistrationCentersMachineUserMapping(String regCenterId, String machineId,
			String userId);

	/**
	 * Create or Update a mapping of registration center,user,and machine
	 * 
	 * @param regCenterMachineUserReqDto
	 *            {@link RegCenterMachineUserReqDto} request
	 * @return {@link RegCenterMachineUserResponseDto}
	 */
	RegCenterMachineUserResponseDto createOrUpdateRegistrationCentersMachineUserMapping(
			RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto> regCenterMachineUserReqDto);

}
