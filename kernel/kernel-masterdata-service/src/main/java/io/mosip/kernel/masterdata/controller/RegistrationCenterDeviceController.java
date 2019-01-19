package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterDeviceID;
import io.mosip.kernel.masterdata.service.RegistrationCenterDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1.0/registrationcenterdevice")
@Api(value = "Operation related to mapping of registration center and devices",tags = { "RegistrationCenterDevice" })
public class RegistrationCenterDeviceController {

	@Autowired
	private RegistrationCenterDeviceService registrationCenterDeviceService;

	@PostMapping
	@ApiOperation(value = "Map provided registration center and device", notes = "Map provided registration center id and device id", response = ResponseRegistrationCenterDeviceDto.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When registration center and device mapped", response = ResponseRegistrationCenterDeviceDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is invalid"),
			@ApiResponse(code = 500, message = "While mapping registration center and device") })
	public ResponseEntity<ResponseRegistrationCenterDeviceDto> createRegistrationCenterAndDevice(
			@Valid @RequestBody RequestDto<RegistrationCenterDeviceDto> requestDto) {
		return new ResponseEntity<>(registrationCenterDeviceService.createRegistrationCenterAndDevice(requestDto),
				HttpStatus.CREATED);
	}
	
	/**
	 * Delete the mapping of registration center and device
	 * 
	 * @param regCenterId
	 *            Registration center id to be deleted
	 * @param deviceId
	 *            DeviceId id to be deleted
	 * @return {@link RegistrationCenterDeviceID}
	 */
	@ApiOperation(value = "Delete the mapping of registration center and device", response = RegistrationCenterDeviceID.class)
	@DeleteMapping("/{regCenterId}/{deviceId}")
	public ResponseEntity<RegistrationCenterDeviceID> deleteRegistrationCenterDeviceMapping(
			@ApiParam("Registration center id to be deleted") @PathVariable String regCenterId,
			@ApiParam("DeviceId id to be deleted") @PathVariable String deviceId) {
		return new ResponseEntity<>(
				registrationCenterDeviceService.deleteRegistrationCenterDeviceMapping(regCenterId, deviceId),
				HttpStatus.OK);
	}
}
