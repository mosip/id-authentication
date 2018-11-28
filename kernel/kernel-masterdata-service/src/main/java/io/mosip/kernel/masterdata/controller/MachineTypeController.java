package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.MachineTypeRequestDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.service.MachineTypeService;


@RestController
public class MachineTypeController {
	

	/**
	 * Reference to MachineTypeService.
	 */
	@Autowired
	private MachineTypeService machinetypeService;
	
	/**
	 * Save list of Machine Type details to the Database
	 * 
	 * @param MachineTypeRequestDto
	 * 				input from user Machine Type DTO
	 *            
	 * @return {@link CodeAndLanguageCodeId}
	 */

	@PostMapping("/machinetype")
	public ResponseEntity<CodeAndLanguageCodeId> saveMachineType(@RequestBody MachineTypeRequestDto machineType) {
		return new ResponseEntity<>( machinetypeService.saveMachineType(machineType), HttpStatus.CREATED);
	}

}
