package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;

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
	 * @param langCode
	 * 			language code from user
	 * @return DeviceResponseDto
	 * 			Returning all Devices Details
	 *
	 */
	public DeviceResponseDto getDeviceLangCode(String langCode);
	
	/**
	 * This abstract method to fetch Devices details for given Language code and DeviceType Code
	 * 
	 * @param langCode
	 * 			language code from user
	 * @param devideTypeCode
	 * 			devideTypeCode from user
	 * @return DeviceLangCodeResponseDto
	 * 			Returning all Devices Details for given Language code and DeviceType Code
	 *
	 */
	public DeviceLangCodeResponseDto getDeviceLangCodeAndDeviceType(String langCode, String devideTypeCode);

	/**
	 * This method is used to add a new Device to master data
	 * 
	 * @param RequestDto<DeviceDto>
	 *          Device DTO to insert data 
	 * @return IdResponseDto
	 * 			Device ID which is successfully inserted
	 */
	public IdResponseDto saveDevice(RequestDto<DeviceDto> deviceRequestDto);

}
