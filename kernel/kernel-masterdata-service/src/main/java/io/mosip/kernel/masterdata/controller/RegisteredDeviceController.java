package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.RegisteredDevicePostReqDto;
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

}
