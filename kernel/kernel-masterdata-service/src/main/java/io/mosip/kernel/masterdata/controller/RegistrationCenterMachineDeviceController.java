package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceID;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Bal Vikash Sharma
 * @author Srinivasan
 * @since 1.0.0
 */
@RestController
@RequestMapping("/registrationcentermachinedevice")
@Api(tags = { "RegistrationCenterMachineDevice" })
public class RegistrationCenterMachineDeviceController {

	@Autowired
	private RegistrationCenterMachineDeviceService registrationCenterMachineDeviceService;

	@ResponseFilter
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	@PostMapping
	@ApiOperation(value = "Map provided registration center, machine and device", notes = "Map provided registration center id, machine id and device id")
	@ApiResponses({ @ApiResponse(code = 201, message = "When registration center, machine and device mapped"),
			@ApiResponse(code = 400, message = "When Request body passed  is invalid"),
			@ApiResponse(code = 500, message = "While mapping registration center, machine and device") })
	public ResponseWrapper<ResponseRegistrationCenterMachineDeviceDto> createRegistrationCenterMachineAndDevice(
			@Valid @RequestBody RequestWrapper<RegistrationCenterMachineDeviceDto> requestDto) {

		ResponseWrapper<ResponseRegistrationCenterMachineDeviceDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterMachineDeviceService
				.createRegistrationCenterMachineAndDevice(requestDto.getRequest()));
		return responseWrapper;
	}

	@ResponseFilter
	@DeleteMapping(value = "/{regcenterid}/{machineid}/{deviceid}")
	@ApiOperation(value = "delete mapping if this service is called.")
	@ApiResponses({ @ApiResponse(code = 201, message = "When registration center, machine and device mapped"),
			@ApiResponse(code = 400, message = "When Request body passed  is invalid"),
			@ApiResponse(code = 500, message = "While mapping registration center, machine and device") })
	public ResponseWrapper<RegistrationCenterMachineDeviceID> deleteRegistrationCentreMachineDeviceMappingDetails(
			@PathVariable(value = "regcenterid") String regCenterId,
			@PathVariable(value = "machineid") String machineId, @PathVariable(value = "deviceid") String deviceId) {

		ResponseWrapper<RegistrationCenterMachineDeviceID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterMachineDeviceService
				.deleteRegistrationCenterMachineAndDevice(regCenterId, machineId, deviceId));
		return responseWrapper;
	}

}
