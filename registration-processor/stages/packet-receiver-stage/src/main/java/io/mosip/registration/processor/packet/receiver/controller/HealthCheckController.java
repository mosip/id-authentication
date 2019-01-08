package io.mosip.registration.processor.packet.receiver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

/**
 * The Class HealthCheckController.
 */
@RestController
@Api(tags = "Health check")
public class HealthCheckController {
	
	/**
	 * Health.
	 *
	 * @return the response entity
	 */
	@GetMapping(value="/health")
	public ResponseEntity<String> health(){
		return ResponseEntity.ok().body("Server is up and running");
	}

}
