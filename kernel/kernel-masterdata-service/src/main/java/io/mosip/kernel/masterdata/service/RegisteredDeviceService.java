package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegisteredDevicePostReqDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegisteredDeviceExtnDto;

/**
 * 
 * @author Megha Tanga
 *
 */
public interface RegisteredDeviceService {

	/**
	 * Method to create Registered Device Provider
	 * 
	 * @param dto
	 *            Regisetered Device Provider Dto from user
	 * @return RegisteredDeviceExtnDto Registered device Dto which has created
	 */
	public RegisteredDeviceExtnDto createRegisteredDevice(RegisteredDevicePostReqDto dto);

}
