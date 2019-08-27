package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeRegisterDeviceRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterResponseDto;
import io.mosip.kernel.masterdata.service.DeviceRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller class for Device registration and de registration.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/device")
@Api(tags = { "DeviceRegister" })
public class DeviceRegisterController {
	/**
	 * Reference to {@link DeviceRegisterService}.
	 */
	@Autowired
	private DeviceRegisterService deviceRegisterService;

	/**
	 * Api to register Device.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the {@link DeviceRegisterResponseDto}.
	 */
	@ApiOperation(value = "Register Device")
	@PostMapping("/register")
	public ResponseEntity<DeviceRegisterResponseDto> registerDevice(
			@ApiParam("foundationTrustCertificate in BASE64 encoded") @RequestBody DeviceRegisterDto request) {
		return new ResponseEntity<>(deviceRegisterService.registerDevice(request), HttpStatus.OK);
	}

	/**
	 * Api to de register Device.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the {@link DeviceRegisterResponseDto}.
	 */
	@ApiOperation(value = "DeRegister Device")
	@DeleteMapping("/deregister")
	public ResponseEntity<DeviceRegisterResponseDto> deRegisterDevice(@RequestBody DeRegisterDeviceRequestDto request) {
		return new ResponseEntity<>(deviceRegisterService.deRegisterDevice(request), HttpStatus.OK);
	}
}
