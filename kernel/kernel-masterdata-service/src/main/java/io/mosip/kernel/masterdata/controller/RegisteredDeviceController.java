package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.DeviceDeRegisterResponse;
import io.mosip.kernel.masterdata.dto.DeviceRegisterResponseDto;
import io.mosip.kernel.masterdata.dto.RegisteredDevicePostReqDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegisteredDeviceExtnDto;
import io.mosip.kernel.masterdata.service.RegisteredDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller for CURD operation on Registered Device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping(value = "/registereddevices")
@Api(tags = { "Registered Device" })
public class RegisteredDeviceController {

	@Autowired
	RegisteredDeviceService registeredDeviceService;

	@ResponseFilter
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	@PostMapping
	@ApiOperation(value = "Service to save Registered Device", notes = "Saves Registered Device Detail and return Registered Device")
	@ApiResponses({ @ApiResponse(code = 201, message = "When Registered Device successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No Registered Device found"),
			@ApiResponse(code = 500, message = "While creating Registered Device any error occured") })
	public ResponseWrapper<RegisteredDeviceExtnDto> createRegisteredDevice(
			@Valid @RequestBody RequestWrapper<RegisteredDevicePostReqDto> registeredDevicePostReqDto) {
		ResponseWrapper<RegisteredDeviceExtnDto> response = new ResponseWrapper<>();
		response.setResponse(registeredDeviceService.createRegisteredDevice(registeredDevicePostReqDto.getRequest()));
		return response;
	}
	
	/**
	 * Api to de-register Device.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the {@link DeviceRegisterResponseDto}.
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	@ApiOperation(value = "DeRegister Device")
	@DeleteMapping("/deregister/{deviceCode}")
	@ResponseFilter
	public ResponseWrapper<DeviceDeRegisterResponse> deRegisterDevice(@Valid @PathVariable String deviceCode) {
		ResponseWrapper<DeviceDeRegisterResponse> response = new ResponseWrapper<>();
		response.setResponse(registeredDeviceService.deRegisterDevice(deviceCode));
		return response;
	}
	
	/**
	 * Api to update status of Device.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the {@link DeviceDeRegisterResponse}.
	 */
	
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	@ApiOperation(value = "Update status of the devive")
	@PutMapping("/update/status")
	public ResponseWrapper<DeviceDeRegisterResponse> deRegisterDevice(@NotBlank @RequestParam(value="devicecode",required=true) String deviceCode,
			@NotBlank @RequestParam(value="statuscode",required=true) String statusCode) {
		ResponseWrapper<DeviceDeRegisterResponse> response = new ResponseWrapper<>();
		response.setResponse(registeredDeviceService.updateStatus(deviceCode, statusCode));
		return response;
	}

}
