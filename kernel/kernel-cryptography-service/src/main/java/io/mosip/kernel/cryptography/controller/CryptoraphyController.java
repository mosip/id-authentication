package io.mosip.kernel.cryptography.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.CryptographyResponseDto;
import io.mosip.kernel.cryptography.service.CryptographyService;

@RestController
public class CryptoraphyController {

	@Autowired
	CryptographyService cryptographyService; 
	
	@PostMapping(value="/encrypt/{applicationId}")
	public CryptographyResponseDto encrypt(@PathVariable("applicationId") String applicationId,@RequestParam("data") byte[] data,@DateTimeFormat(iso=ISO.DATE_TIME)@RequestParam("timeStamp") LocalDateTime timeStamp,@RequestParam("machineId")Optional<String>machineId) {
		return cryptographyService.encrypt(applicationId,data,timeStamp,machineId);
	}
	

	@PostMapping(value="/decrypt/{applicationId}")
	public CryptographyResponseDto decrypt(@PathVariable("applicationId") String applicationId,@RequestParam("data") byte[] data,@DateTimeFormat(iso=ISO.DATE_TIME)@RequestParam("timeStamp") LocalDateTime timeStamp,@RequestParam("machineId")Optional<String>machineId) {
		return cryptographyService.decrypt(applicationId,data,timeStamp,machineId);
	}
}
