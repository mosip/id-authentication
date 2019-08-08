package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceRegisterDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterResponseDto;
import io.mosip.kernel.masterdata.service.DeviceRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/devices")
@Api(tags = { "DeviceRegister" })
public class DeviceRegisterController {
	@Autowired
	private DeviceRegisterService deviceRegisterService;

	@ApiOperation(value = "Register Device")
	@PostMapping("/registration")
	public ResponseEntity<DeviceRegisterResponseDto> registerDevice(
			@ApiParam("foundationTrustCertificate in BASE64 encoded") @RequestBody DeviceRegisterDto request) {
		return new ResponseEntity<>(deviceRegisterService.registerDevice(request), HttpStatus.OK);
	}
}
