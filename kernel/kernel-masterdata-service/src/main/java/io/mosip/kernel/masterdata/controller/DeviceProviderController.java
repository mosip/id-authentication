package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.service.DeviceProviderService;

/**
 * Device provider controller
 * @author Srinivasan
 *
 */
@RestController
@RequestMapping(value = "/deviceprovider")
public class DeviceProviderController {

	@Autowired
	private DeviceProviderService deviceProviderService;

	@GetMapping("/validate/{deviceCode}/{deviceProviderId}/{deviceServiceId}/{deviceServiceVersion}")
	public ResponseWrapper<ResponseDto> validateDeviceProvider(@PathVariable String deviceCode,
			@PathVariable String deviceProviderId, @PathVariable String deviceServiceId,
			@PathVariable String deviceServiceVersion) {
		ResponseWrapper<ResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceProviderService.validateDeviceProviders(deviceCode, deviceProviderId,
				deviceServiceId, deviceServiceVersion));
		return responseWrapper;

	}
}
