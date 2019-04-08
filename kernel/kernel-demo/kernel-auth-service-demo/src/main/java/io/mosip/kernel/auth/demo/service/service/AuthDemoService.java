package io.mosip.kernel.auth.demo.service.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import io.mosip.kernel.auth.demo.service.dto.AuthDemoServiceRequestDto;
import io.mosip.kernel.auth.demo.service.dto.AuthDemoServiceResponseDto;
import io.mosip.kernel.auth.demo.service.dto.OtpRequestDto;
import io.mosip.kernel.auth.demo.service.dto.OtpResponseDto;


@Service
public interface AuthDemoService {



	AuthDemoServiceResponseDto addApplication(@Valid AuthDemoServiceRequestDto auditRequestDto);

	AuthDemoServiceResponseDto getApplication(String applicationId);

	AuthDemoServiceResponseDto deleteMapping(String applicationId);

	AuthDemoServiceResponseDto updateApplication(@Valid AuthDemoServiceRequestDto auditRequestDto);
    
	OtpResponseDto sendOtp(@Valid OtpRequestDto smsRequestDto);

}