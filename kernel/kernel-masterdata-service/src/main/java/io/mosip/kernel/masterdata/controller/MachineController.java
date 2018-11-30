package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.MachineDetailResponseIdDto;
import io.mosip.kernel.masterdata.dto.MachineRequestDto;
import io.mosip.kernel.masterdata.dto.MachineSpecIdAndId;
import io.mosip.kernel.masterdata.dto.MachineTypeCodeAndLanguageCodeAndId;
import io.mosip.kernel.masterdata.dto.getresponse.MachineResponseDto;
import io.mosip.kernel.masterdata.service.MachineService;

/**

 * This controller class provides Machine details based on user provided data.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
@RequestMapping(value = "/machines")
public class MachineController {

	/**
	 * Reference to MachineDetailService.
	 */
	@Autowired
	private MachineService machineService;

	/**
	 * 
	 * Function to fetch machine detail based on given Machine ID and Language code.
	 * 
	 * @param machineId
	 * @param langcode
	 * @return machine detail based on given Machine ID and Language code
	 */
	@GetMapping(value = "/{id}/{langcode}")
	public MachineDetailResponseIdDto getMachineDetailIdLang(@PathVariable("id") String machineId,
			@PathVariable("langcode") String langCode) {
		return machineService.getMachineDetailIdLang(machineId, langCode);

	}

	/**
	 * 
	 * Function to fetch machine detail based on given Language code
	 * 
	 * @param langcode
	 * @return machine detail based on given Language code
	 */

	@GetMapping(value = "/{langcode}")
	public MachineResponseDto getMachineDetailLang(@PathVariable("langcode") String langCode) {
		return machineService.getMachineDetailLang(langCode);

	}

	/**
	 * Function to fetch a all machines details
	 * 
	 * @return all machines details
	 */

	@GetMapping
	public MachineResponseDto getMachineDetailAll() {
		return machineService.getMachineDetailAll();

	}
	
	/**
	 * Save machine  details to the database table
	 * 
	 * @param machine
	 *            input from user Machine  DTO
	 * @return {@link MachineTypeCodeAndLanguageCodeAndId}
	 */
	@PostMapping("/")
	public ResponseEntity<MachineSpecIdAndId> saveMachine(
			@RequestBody MachineRequestDto machine) {

		return new ResponseEntity<>(machineService.saveMachine(machine), HttpStatus.CREATED);
	}

}
