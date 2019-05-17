package io.mosip.registration.processor.core.abstractverticle;

import java.util.Map;

import lombok.Data;

/**
 * 
 * @author Mukul Puspam
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