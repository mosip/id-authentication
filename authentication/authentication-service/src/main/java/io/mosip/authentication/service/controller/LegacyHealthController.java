package io.mosip.authentication.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * v1.3.1-rc.1 deployed internal-service and otp-service as separate apps, so
 * the api-test-rig and monitoring probe health at
 * ".../internal/actuator/health" and ".../otp/actuator/health" in addition
 * to the default ".../actuator/health". Now that both are merged into this
 * single service, alias those legacy paths to the same health status.
 */
@RestController
public class LegacyHealthController {

	@Autowired
	private HealthEndpoint healthEndpoint;

	@GetMapping({ "/internal/actuator/health", "/otp/actuator/health" })
	public HealthComponent health() {
		return healthEndpoint.health();
	}
}
