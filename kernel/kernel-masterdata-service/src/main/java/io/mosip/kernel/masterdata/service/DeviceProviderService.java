package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;

public interface DeviceProviderService {

	ResponseDto validateDeviceProviders(String deviceCode, String deviceProviderId, String deviceServiceId,
			String deviceServiceVersion);
}
