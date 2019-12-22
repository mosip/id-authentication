package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.deviceprovidermanager.spi.DeviceProviderService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.DeviceProviderDto;
import io.mosip.kernel.masterdata.dto.ValidateDeviceDto;
import io.mosip.kernel.masterdata.dto.ValidateDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceProviderExtnDto;
import io.mosip.kernel.masterdata.utils.AuditUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Device provider controller for CURD operation
 * 
 * @author Megha Tanga
 *
 */
@RestController
@RequestMapping(value = "/deviceprovider")
@Api(tags = { "DeviceProvider" })
public class DeviceProviderController {

	@Autowired
	AuditUtil auditUtil;
	
	@Autowired
	private DeviceProviderService<ResponseDto,ValidateDeviceDto,ValidateDeviceHistoryDto,DeviceProviderDto,DeviceProviderExtnDto> deviceProviderSerice;

	@PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	@PostMapping
	@ApiOperation(value = "Service to save Device Provide", notes = "Saves Device Provider Detail and return Device Provider")
	@ApiResponses({ @ApiResponse(code = 201, message = "When Device Provider successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No Device Provider found"),
			@ApiResponse(code = 500, message = "While creating Device Provider any error occured") })
	public ResponseWrapper<DeviceProviderExtnDto> createDeviceProvider(
			@Valid @RequestBody RequestWrapper<DeviceProviderDto> deviceProviderDto) {
		auditUtil.auditRequest(MasterDataConstant.CREATE_API_IS_CALLED + DeviceProviderDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.CREATE_API_IS_CALLED + DeviceProviderDto.class.getCanonicalName());
		ResponseWrapper<DeviceProviderExtnDto> response = new ResponseWrapper<>();
		response.setResponse(deviceProviderSerice.createDeviceProvider(deviceProviderDto.getRequest()));
		auditUtil.auditRequest(MasterDataConstant.CREATE_API_IS_CALLED + DeviceProviderDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.CREATE_API_IS_CALLED + DeviceProviderDto.class.getCanonicalName());
		return response;
	}
	
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	@PutMapping
	@ApiOperation(value = "Service to save Device Provide", notes = "Saves Device Provider Detail and return Device Provider")
	@ApiResponses({ @ApiResponse(code = 201, message = "When Device Provider successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No Device Provider found"),
			@ApiResponse(code = 500, message = "While creating Device Provider any error occured") })
	public ResponseWrapper<DeviceProviderExtnDto> updateDeviceProvider(
			@Valid @RequestBody RequestWrapper<DeviceProviderDto> deviceProviderDto) {
		auditUtil.auditRequest(MasterDataConstant.UPDATE_API_IS_CALLED + DeviceProviderDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.UPDATE_API_IS_CALLED + DeviceProviderDto.class.getCanonicalName());
		ResponseWrapper<DeviceProviderExtnDto> response = new ResponseWrapper<>();
		response.setResponse(deviceProviderSerice.updateDeviceProvider(deviceProviderDto.getRequest()));
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_UPDATE, DeviceProviderDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_UPDATE_DESC, DeviceProviderDto.class.getCanonicalName()));
		return response;
	}

}
