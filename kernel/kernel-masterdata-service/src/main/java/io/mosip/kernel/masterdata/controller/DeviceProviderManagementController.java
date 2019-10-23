package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.ValidateDeviceDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.service.DeviceProviderService;

/**
 * Device provider management controller
 * 
 * @author Srinivasan
 *
 */
@RestController
@RequestMapping(value = "/deviceprovidermanagement")
public class DeviceProviderManagementController {

	@Autowired
	private DeviceProviderService deviceProviderService;

	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','ID_AUTHENTICATION','REGISTRATION_PROCESSOR')")
	@PostMapping("/validate")
	public ResponseWrapper<ResponseDto> validateDeviceProvider(
			@RequestBody @Valid RequestWrapper<ValidateDeviceDto> request) {
		ResponseWrapper<ResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				deviceProviderService.validateDeviceProviders(request.getRequest()));
		return responseWrapper;

	}

	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','ID_AUTHENTICATION','REGISTRATION_PROCESSOR')")
	@GetMapping("/validate/history")
	public ResponseWrapper<ResponseDto> validateDeviceProviderHistory(@RequestParam @NotBlank String deviceCode,
			@RequestParam @NotBlank String deviceProviderId, @RequestParam @NotBlank String deviceServiceVersion,
			@RequestParam String timeStamp) {
		ResponseWrapper<ResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceProviderService.validateDeviceProviderHistory(deviceCode, deviceProviderId,
				deviceServiceVersion, timeStamp));
		return responseWrapper;
	}

}
