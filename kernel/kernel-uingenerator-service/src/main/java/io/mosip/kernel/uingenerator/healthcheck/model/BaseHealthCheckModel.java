package io.mosip.kernel.uingenerator.healthcheck.model;

import java.util.Map;

import lombok.Data;


@Data
public class BaseHealthCheckModel {

	private String status;
	
	private Map<String, Object> details;

	
	
}
