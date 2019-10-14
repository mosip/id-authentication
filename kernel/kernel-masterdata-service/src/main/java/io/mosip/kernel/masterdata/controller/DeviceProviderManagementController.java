package io.mosip.kernel.masterdata.controller;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping(value = "/deviceprovidermanagement")
public class DeviceProviderManagementController {

	@Autowired
	private DeviceProviderService deviceProviderService;

	@GetMapping("/validate")
	public ResponseWrapper<ResponseDto> validateDeviceProvider(@RequestParam @NotBlank String deviceCode,
			@RequestParam @NotBlank String deviceProviderId, @RequestParam @NotBlank String deviceServiceId,
			@RequestParam @NotBlank String deviceServiceVersion) {
		ResponseWrapper<ResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceProviderService.validateDeviceProviders(deviceCode, deviceProviderId,
				deviceServiceId, deviceServiceVersion));
		return responseWrapper;

	}
	
	@GetMapping("/validate/history")
	public ResponseWrapper<ResponseDto> validateDeviceProviderHistory(@RequestParam @NotBlank String deviceCode,@RequestParam @NotBlank String deviceProviderId ,
			@RequestParam @NotBlank String deviceServiceId,@RequestParam @NotBlank String deviceServiceVersion,@RequestParam String timeStamp)
	{
		ResponseWrapper<ResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceProviderService.validateDeviceProviderHistory(deviceCode,deviceProviderId,deviceServiceId,deviceServiceVersion,timeStamp));
		return responseWrapper;
	}
	
	
}
