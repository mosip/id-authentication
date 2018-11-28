package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.MachineSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.MachineTypeCodeAndLanguageCodeAndId;
import io.mosip.kernel.masterdata.service.MachineSpecificationService;

@RestController
public class MachineSpecificationController {
	
	

	@Autowired
	MachineSpecificationService machineSpecificationService;
	/**
	 * Save machine specification details to the database table
	 * 
	 * @param machineSpecification
	 *            input from user Machine specification DTO
	 * @return {@link MachineTypeCodeAndLanguageCodeAndId}
	 */
	@PostMapping("/machinespecification")
	public ResponseEntity<MachineTypeCodeAndLanguageCodeAndId> saveMachineSpecification(
			@RequestBody MachineSpecificationRequestDto machineSpecification) {

		return new ResponseEntity<>(machineSpecificationService.saveMachineSpecification(machineSpecification), HttpStatus.CREATED);
	}

}
