package io.mosip.kernel.idgenerator.tsp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.idgenerator.tsp.dto.TspResponseDTO;

/**
 * Controller class for TSPID generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
public class TspGeneratorController {

	/**
	 * Reference to TspIdGenerator.
	 */
	@Autowired
	private TspIdGenerator<TspResponseDTO> tspIdGeneratorService;

	/**
	 * This api generate TSPID when requested.
	 * 
	 * @return the TSPID.
	 */
	@GetMapping("/idgenerator/tsp")
	public ResponseEntity<TspResponseDTO> generateId() {

		return new ResponseEntity<>(tspIdGeneratorService.generateId(), HttpStatus.OK);
	}
}
