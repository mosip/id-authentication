/**
 * 
 */
package io.mosip.registration.processor.status.controller;

import java.util.List;

import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class SyncRegistrationController.
 *
 * @author M1047487
 */
@RestController
@RequestMapping("/v0.1/registration-processor/registration-status")
@Api(tags = "Status Handler")
public class SyncRegistrationController {
	
	@Autowired
	SyncRegistrationService<SyncRegistrationDto> syncRegistrationService;

	/**
	 * Instantiates a new sync registration controller.
	 */
	public SyncRegistrationController() {
		super();
	}

	/**
	 * Sync.
	 *
	 * @param syncResgistrationdto
	 *            the sync resgistrationdto
	 * @return the response entity
	 */
	@GetMapping(path = "/sync", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the synchronizing registration entity", response = RegistrationStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Synchronizing Registration Entity successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the Synchronizing Registration Entity") })
	public ResponseEntity<List<SyncRegistrationDto>> sync(
			@RequestParam(value = "syncRegistrationIds", required = true) String syncRegistrationIds) {
		//List<SyncRegistrationDto> syncResgistrationdto = syncRegistrationService.sync(syncResgistrationdto);
		return null;
	}

}
