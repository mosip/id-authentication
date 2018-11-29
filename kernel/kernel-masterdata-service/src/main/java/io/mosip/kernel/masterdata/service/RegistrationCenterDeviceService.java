package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterDeviceDto;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */

public interface RegistrationCenterDeviceService {

	public ResponseRegistrationCenterDeviceDto mapRegistrationCenterAndDevice(
			RequestDto<RegistrationCenterDeviceDto> requestDto);

}
