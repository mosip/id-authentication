package org.mosip.kernel.uingenerator.controller;

import org.mosip.kernel.uingenerator.dto.UinResponseDto;
import org.mosip.kernel.uingenerator.service.UinGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UinGeneratorController {

	@Autowired
	private UinGeneratorService uinGeneratorService;

	@GetMapping(value = "/idgenerator/uin")
	public ResponseEntity<UinResponseDto> getUin() {

		UinResponseDto idDto = uinGeneratorService.getUin();

		return new ResponseEntity<>(idDto, HttpStatus.OK);
	}

}
