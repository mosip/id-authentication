package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceDto;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceExtDto;
import io.mosip.kernel.masterdata.service.MOSIPDeviceServices;
import io.mosip.kernel.masterdata.utils.AuditUtil;
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
	AuditUtil auditUtil;
	
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
		auditUtil.auditRequest(MasterDataConstant.CREATE_API_IS_CALLED + MOSIPDeviceServiceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.CREATE_API_IS_CALLED + MOSIPDeviceServiceDto.class.getCanonicalName(),"ADM-707");
		ResponseWrapper<MOSIPDeviceServiceExtDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(mosipDeviceServices.createMOSIPDeviceService(mosipDeviceServiceRequestDto.getRequest()));
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_CREATE, MOSIPDeviceServiceDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_CREATE_DESC, MOSIPDeviceServiceDto.class.getCanonicalName()),"ADM-708");
		return responseWrapper;

	}

	/**
	 * Put API to update a row of MOSIPDeviceService data
	 * 
	 * @param MOSIPDeviceServiceRequestDto
	 *            input parameter deviceRequestDto
	 * 
	 * @return ResponseEntity MOSIPDeviceService which is updated successfully
	 *         {@link ResponseEntity}
	 */
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	@ResponseFilter
	@PutMapping
	@ApiOperation(value = "Service to update MOSIPDeviceService", notes = "Updates MOSIPDeviceService and returns success message")
	@ApiResponses({ @ApiResponse(code = 201, message = "When MOSIPDeviceService successfully updated"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While updating MOSIPDeviceService any error occured") })
	public ResponseWrapper<String> udpateMOSIPDeviceService(
			@Valid @RequestBody RequestWrapper<MOSIPDeviceServiceDto> mosipDeviceServiceRequestDto) {
		auditUtil.auditRequest(MasterDataConstant.UPDATE_API_IS_CALLED + MOSIPDeviceServiceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.UPDATE_API_IS_CALLED + MOSIPDeviceServiceDto.class.getCanonicalName(),"ADM-709");
		ResponseWrapper<String> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(mosipDeviceServices.updateMOSIPDeviceService(mosipDeviceServiceRequestDto.getRequest()));
		auditUtil.auditRequest(MasterDataConstant.UPDATE_API_IS_CALLED + MOSIPDeviceServiceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.UPDATE_API_IS_CALLED + MOSIPDeviceServiceDto.class.getCanonicalName(),"ADM-710");
		return responseWrapper;

	}
}