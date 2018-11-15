package io.mosip.kernel.masterdata.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.MachineDetailDto;
import io.mosip.kernel.masterdata.dto.MachineDetailResponseDto;
import io.mosip.kernel.masterdata.service.MachineDetailService;

/**
 * Controller with api to get Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
@RequestMapping(value = "/machines")
public class MachineDetailController {

	@Autowired
	private MachineDetailService macService;

	/**
	 * Get api to fetch a machine details based on given Machine ID and Language
	 * code
	 * 
	 * @param machineId
	 * @param langcode
	 * @return machine detail based on given Machine ID and Language code
	 */
	@GetMapping(value = "/{id}/{langcode}")
	public MachineDetailDto getMachineDetailIdLang(@PathVariable("id") String machineId,
			@PathVariable("langcode") String langCode) {
		return macService.getMachineDetailIdLang(machineId, langCode);

	}

	/**
	 * Get api to fetch a all machines details
	 * 
	 * @return all machines details
	 */

	@GetMapping
	public MachineDetailResponseDto getMachineDetailAll() {
		return macService.getMachineDetailAll();

	}

}
