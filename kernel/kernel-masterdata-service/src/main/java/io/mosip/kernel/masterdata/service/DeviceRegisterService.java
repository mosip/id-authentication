package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeRegisterDeviceRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterResponseDto;

/**
 * Service to register and de register Device.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public interface DeviceRegisterService {
	/**
	 * Method to register Device.
	 * 
	 * @param request
	 *            the {@link DeviceRegisterDto}.
	 * @return the {@link DeviceRegisterResponseDto}.
	 */
	public DeviceRegisterResponseDto registerDevice(DeviceRegisterDto request);

	/**
	 * Method to de register Device.
	 * 
	 * @param request
	 *            the {@link DeRegisterDeviceRequestDto}.
	 * @return the {@link DeviceRegisterResponseDto}.
	 */
	public DeviceRegisterResponseDto deRegisterDevice(DeRegisterDeviceRequestDto request);
}
