package io.mosip.kernel.ridgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.ridgenerator.dto.RidGeneratorResponseDto;
import io.mosip.kernel.ridgenerator.service.RidGeneratorService;
import io.swagger.annotations.ApiOperation;

/**
 * Controller class for RID generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@RestController
@CrossOrigin
public class RidGeneratorController {

	/**
	 * Reference to {@link RidGeneratorService}.
	 */
	@Autowired
	private RidGeneratorService<RidGeneratorResponseDto> ridGeneratorService;

	/**
	 * Api to generate RID.
	 * 
	 * @param centerId  the registration center id.
	 * @param machineId the machine id.
	 * @return the response.
	 */
	@ResponseFilter
	@GetMapping("/generate/rid/{centerid}/{machineid}")
	@ApiOperation(value = "Service to generate RID")
	@PreAuthorize("hasRole('REGISTRATION_PROCESSOR')")
	public ResponseWrapper<RidGeneratorResponseDto> generateRid(@PathVariable("centerid") String centerId,
			@PathVariable("machineid") String machineId) {
		ResponseWrapper<RidGeneratorResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(ridGeneratorService.generateRid(centerId.trim(), machineId.trim()));
		return responseWrapper;
	}
}
