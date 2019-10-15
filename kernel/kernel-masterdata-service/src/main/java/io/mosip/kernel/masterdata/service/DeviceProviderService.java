package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceProviderDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceProviderExtnDto;

public interface DeviceProviderService {

	ResponseDto validateDeviceProviders(String deviceCode, String deviceProviderId, String deviceServiceId,
			String deviceServiceVersion);

	ResponseDto validateDeviceProviderHistory(String deviceCode, String deviceProviderId, String deviceServiceId,
			String deviceServiceVersion, String timeStamp);
	
	/**
	 * Method to create Device Provider 
	 * 
	 * @param dto
	 *        Device Provider dto from user
	 * @return DeviceProviderExtnDto
	 *          device Provider dto which has created
	 */
	public DeviceProviderExtnDto createDeviceProvider(DeviceProviderDto dto);
}
