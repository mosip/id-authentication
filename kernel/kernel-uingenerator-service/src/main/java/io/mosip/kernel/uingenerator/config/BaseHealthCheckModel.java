package io.mosip.kernel.uingenerator.config;

import java.util.Map;

import lombok.Data;

/**
 * Base Model for health check
 * 
 * @author Urvil joshi
 * 
 * @since 1.0.0
 *
 */
@Data
public class BaseHealthCheckModel {

	/**
	 * Final result of check (UP or DOWN)
	 */
	private String status;

	/**
	 * Details about check
	 */
	private Map<String, Object> details;

}
