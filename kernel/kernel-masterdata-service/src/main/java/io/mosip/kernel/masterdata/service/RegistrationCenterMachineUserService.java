package io.mosip.kernel.masterdata.service;

import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
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

	

}
