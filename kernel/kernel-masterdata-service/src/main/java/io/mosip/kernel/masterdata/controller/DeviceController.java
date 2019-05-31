package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller with api to save and get Device Details
 * 
 * @author Megha Tanga
 * @author Sidhant Agarwal
 * @author Neha Sinha
 * @since 1.0.0
 *
 */

@RestController
@RequestMapping(value = "/devices")
@Api(tags = { "Device" })
public class DeviceController {

	/**
	 * Reference to DeviceService.
	 */
	@Autowired
	private DeviceService deviceService;

	/**
	 * Get api to fetch a all device details based on language code
	 * 
	 * @param langCode
	 *            pass language code as String
	 * 
	 * @return DeviceResponseDto all device details based on given language code
	 *         {@link DeviceResponseDto}
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping(value = "/{languagecode}")
	@ApiOperation(value = "Retrieve all Device for the given Languge Code", notes = "Retrieve all Device for the given Languge Code")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device retrieved from database for the given Languge Code"),
			@ApiResponse(code = 404, message = "When No Device Details found for the given Languge Code"),
			@ApiResponse(code = 500, message = "While retrieving Device any error occured") })
	public ResponseWrapper<DeviceResponseDto> getDeviceLang(@PathVariable("languagecode") String langCode) {

		ResponseWrapper<DeviceResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.getDeviceLangCode(langCode));
		return responseWrapper;
	}

	/**
	 * Get api to fetch a all device details based on device type and language code
	 * 
	 * @param langCode
	 *            pass language code as String
	 * 
	 * @param deviceType
	 *            pass device Type id as String
	 * 
	 * @return DeviceLangCodeResponseDto all device details based on given device
	 *         type and language code {@link DeviceLangCodeResponseDto}
	 */
	@ResponseFilter
	@GetMapping(value = "/{languagecode}/{deviceType}")
	@ApiOperation(value = "Retrieve all Device for the given Languge Code and Device Type", notes = "Retrieve all Device for the given Languge Code and Device Type")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device retrieved from database for the given Languge Code"),
			@ApiResponse(code = 404, message = "When No Device Details found for the given Languge Code and Device Type"),
			@ApiResponse(code = 500, message = "While retrieving Device any error occured") })
	public ResponseWrapper<DeviceLangCodeResponseDto> getDeviceLangCodeAndDeviceType(
			@PathVariable("languagecode") String langCode, @PathVariable("deviceType") String deviceType) {

		ResponseWrapper<DeviceLangCodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.getDeviceLangCodeAndDeviceType(langCode, deviceType));
		return responseWrapper;
	}

	/**
	 * Post API to insert a new row of Device data
	 * 
	 * @param deviceRequestDto
	 *            input parameter deviceRequestDto
	 * 
	 * @return ResponseEntity Device Id which is inserted successfully
	 *         {@link ResponseEntity}
	 */
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	@ResponseFilter
	@PostMapping
	@ApiOperation(value = "Service to save Device", notes = "Saves Device and return Device id")
	@ApiResponses({ @ApiResponse(code = 201, message = "When Device successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating device any error occured") })
	public ResponseWrapper<IdAndLanguageCodeID> createDevice(
			@Valid @RequestBody RequestWrapper<DeviceDto> deviceRequestDto) {

		ResponseWrapper<IdAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.createDevice(deviceRequestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * API to update an existing row of Device data
	 * 
	 * @param deviceRequestDto
	 *            input parameter deviceRequestDto
	 * 
	 * @return ResponseEntity Device Id which is updated successfully
	 *         {@link ResponseEntity}
	 */
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	@ResponseFilter
	@PutMapping
	@ApiOperation(value = "Service to update Device", notes = "Update Device and return Device id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When Device updated successfully"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When Device is not found"),
			@ApiResponse(code = 500, message = "While updating device any error occured") })
	public ResponseWrapper<IdAndLanguageCodeID> updateDevice(
			@Valid @RequestBody RequestWrapper<DeviceDto> deviceRequestDto) {

		ResponseWrapper<IdAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.updateDevice(deviceRequestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * API to delete Device
	 * 
	 * @param id
	 *            The Device Id
	 * 
	 * @return {@link ResponseEntity} The id of the Device which is deleted
	 */
	@ResponseFilter
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Service to delete device", notes = "Delete Device and return Device Id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When Device deleted successfully"),
			@ApiResponse(code = 404, message = "When Device not found"),
			@ApiResponse(code = 500, message = "Error occurred while deleting Device") })
	public ResponseWrapper<IdResponseDto> deleteDevice(@PathVariable("id") String id) {

		ResponseWrapper<IdResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.deleteDevice(id));
		return responseWrapper;
	}
}
