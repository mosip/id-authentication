package io.mosip.kernel.idgenerator.uin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.idgenerator.uin.dto.UinResponseDto;
import io.mosip.kernel.idgenerator.uin.service.UinGeneratorService;

/**
 * Controller with api to get a uin
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestController
public class UinGeneratorController {

	/**
	 * Field for {@link #uinGeneratorService}
	 */
	@Autowired
	private UinGeneratorService uinGeneratorService;

	/**
	 * Get api to fetch a uin from pool of generated uins
	 * 
	 * @return uin
	 */
	@GetMapping(value = "/v1.0/uin")
	public ResponseEntity<UinResponseDto> getUin() {
		UinResponseDto idDto = uinGeneratorService.getUin();
		return new ResponseEntity<>(idDto, HttpStatus.OK);
	}

}
