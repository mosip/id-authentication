package io.mosip.kernel.masterdata.service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import io.mosip.kernel.masterdata.dto.DeviceDeRegisterResponse;
import io.mosip.kernel.masterdata.dto.EncodedRegisteredDeviceResponse;
import io.mosip.kernel.masterdata.dto.RegisteredDevicePostReqDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
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

	public DeviceDeRegisterResponse deRegisterDevice(@Valid String deviceCode);

	public ResponseDto updateStatus(@NotBlank String deviceCode, @NotBlank String statusCode);

}
