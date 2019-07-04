package io.mosip.registration.processor.print.service.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class JsonFileDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String version;
	
	private String requestTime;
	
	private String metadata;
	
	private JsonRequestDTO request;

}
