package io.mosip.registration.processor.request.handler.service.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

/**
 * @author Ranjitha
 *
 */
@RestController
@Api(tags = "Resident Service RePrint")
public class ResidentServiceRePrintController {
	
	@PostMapping(path = "/reprint", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getStatus() {
				return null;
	
	}
}
