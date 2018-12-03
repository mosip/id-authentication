package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;

/**
 * This interface has abstract methods to fetch a Device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface DeviceService {

	/**
	 * This abstract method to fetch all Devices details
	 * 
	 * @return Returning all Devices Details
	 *
	 */
	public DeviceResponseDto getDeviceLangCode(String langCode);

	public DeviceLangCodeResponseDto getDeviceLangCodeAndDeviceType(String langCode, String devideTypeCode);

	/**
	 * This method is used to add a new Device to master data
	 * 
	 * @param deviceRequestDto
	 *            DTO containing input parameters to add a new device
	 * @return code of the entered device
	 */
	public CodeResponseDto saveDevice(RequestDto<DeviceDto> deviceRequestDto);

}
