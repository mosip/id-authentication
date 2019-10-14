package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceDto;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceExtDto;
import io.mosip.kernel.masterdata.service.MOSIPDeviceServices;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller with api to save and get Device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
@RequestMapping(value = "/mosipdeviceservice")
@Api(tags = { "MOSIPDeviceService" })
public class MOSIPDeviceServiceController {

	@Autowired
	MOSIPDeviceServices mosipDeviceServices;

	/**
	 * Post API to insert a new row of MOSIPDeviceService data
	 * 
	 * @param MOSIPDeviceServiceRequestDto
	 *            input parameter deviceRequestDto
	 * 
	 * @return ResponseEntity MOSIPDeviceService which is inserted successfully
	 *         {@link ResponseEntity}
	 */
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	@ResponseFilter
	@PostMapping
	@ApiOperation(value = "Service to save MOSIPDeviceService", notes = "Saves MOSIPDeviceService and return MOSIPDeviceService id")
	@ApiResponses({ @ApiResponse(code = 201, message = "When MOSIPDeviceService successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating MOSIPDeviceService any error occured") })
	public ResponseWrapper<MOSIPDeviceServiceExtDto> createMOSIPDeviceService(
			@Valid @RequestBody RequestWrapper<MOSIPDeviceServiceDto> mosipDeviceServiceRequestDto) {

		ResponseWrapper<MOSIPDeviceServiceExtDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(mosipDeviceServices.createMOSIPDeviceService(mosipDeviceServiceRequestDto.getRequest()));
		return responseWrapper;

	}

}