package io.mosip.kernel.idgenerator.tsp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.idgenerator.tsp.dto.TspResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * Controller class for TSPID generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
@Api(tags= {"TspIdGenerator"})
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
	@ApiOperation(value = "Generate TSP ID", response = TspResponseDTO.class)
	@ApiResponse(code = 200, message = "TSp Id successfully generated")
	@GetMapping(value = "/v1.0/tsp")
	public ResponseEntity<TspResponseDTO> generateId() {

		return new ResponseEntity<>(tspIdGeneratorService.generateId(), HttpStatus.OK);
	}
}
