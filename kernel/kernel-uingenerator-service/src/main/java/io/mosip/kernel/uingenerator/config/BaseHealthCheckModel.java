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

	private String status;

	private Map<String, Object> details;

}
