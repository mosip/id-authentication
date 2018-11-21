package io.mosip.kernel.masterdata.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.MachineDetailResponseDto;
import io.mosip.kernel.masterdata.dto.MachineDetailResponseIdDto;
import io.mosip.kernel.masterdata.service.MachineDetailService;

/**
 * This controller class provides Machine  details based on user
 * provided data.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
@RequestMapping(value = "/machines")
public class MachineDetailController {

	/**
	 * Reference to MachineDetailService.
	 */
	@Autowired
	private MachineDetailService macService;

	/**
	 * 
	 * Function to fetch machine detail  based on given Machine ID and Language
	 * code.
	 * 
	 * @param machineId
	 * @param langcode
	 * @return machine detail based on given Machine ID and Language code
	 */
	@GetMapping(value = "/{id}/{langcode}")
	public MachineDetailResponseIdDto getMachineDetailIdLang(@PathVariable("id") String machineId,
			@PathVariable("langcode") String langCode) {
		return macService.getMachineDetailIdLang(machineId, langCode);

	}

	/**
	 * Function to fetch a all machines details
	 * 
	 * @return all machines details
	 */

	@GetMapping
	public MachineDetailResponseDto getMachineDetailAll() {
		return macService.getMachineDetailAll();

	}

}
