package io.mosip.kernel.masterdata.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
/**
 * Service for user machine mapping
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public interface RegistrationCenterMachineUserService {

	RegistrationCenterMachineUserID createRegistrationCentersMachineUserMapping(
			@Valid RequestDto<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDto);

	

}
