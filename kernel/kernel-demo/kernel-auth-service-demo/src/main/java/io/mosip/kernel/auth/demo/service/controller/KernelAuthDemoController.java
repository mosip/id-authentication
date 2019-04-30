package io.mosip.kernel.auth.demo.service.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.auth.demo.service.dto.AuthDemoServiceRequestDto;
import io.mosip.kernel.auth.demo.service.dto.AuthDemoServiceResponseDto;
import io.mosip.kernel.auth.demo.service.dto.OtpRequestDto;
import io.mosip.kernel.auth.demo.service.dto.OtpResponseDto;
import io.mosip.kernel.auth.demo.service.service.AuthDemoService;



@RestController
public class KernelAuthDemoController {
	
	
	
	@Autowired
	private AuthDemoService service;
	
	
	@PreAuthorize("hasAnyRole('INDIVIDUAL', 'REGISTRATION_PROCESSOR')")
	@PostMapping(value = "/application")
	public ResponseEntity<AuthDemoServiceResponseDto> addApplication(@RequestBody @Valid AuthDemoServiceRequestDto auditRequestDto) {
		return new ResponseEntity<>(service.addApplication(auditRequestDto), HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('INDIVIDUAL', 'REGISTRATION_PROCESSOR')")
	@GetMapping(value = "/application/{applicationid}")
	public ResponseEntity<AuthDemoServiceResponseDto> getApplication(@PathVariable("applicationid") String applicationId) {
		return new ResponseEntity<>(service.getApplication(applicationId), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('INDIVIDUAL', 'REGISTRATION_PROCESSOR')")
	@PutMapping(value = "/application")
	public ResponseEntity<AuthDemoServiceResponseDto> updateApplication(@RequestBody @Valid AuthDemoServiceRequestDto auditRequestDto) {
		return new ResponseEntity<>(service.updateApplication(auditRequestDto), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('INDIVIDUAL', 'REGISTRATION_PROCESSOR')")
	@DeleteMapping(value = "/application/{applicationid}")
	public ResponseEntity<AuthDemoServiceResponseDto> deletMapping(@PathVariable("applicationid") String applicationId) {
		return new ResponseEntity<>(service.deleteMapping(applicationId), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('INDIVIDUAL', 'REGISTRATION_PROCESSOR')")
	@PostMapping(value = "/otp")
	public ResponseEntity<OtpResponseDto> otp(@RequestBody @Valid OtpRequestDto otpRequestDto) {
		return new ResponseEntity<>(service.sendOtp(otpRequestDto), HttpStatus.OK);
	}


}
