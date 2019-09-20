package io.mosip.registration.processor.print.service.dto;

import java.util.LinkedHashMap;

import lombok.Data;

@Data
public class PrintTextFileDto {
	private String id;
	private String version;
	private String requestTime;
	private LinkedHashMap<String, String> request;
	

}
