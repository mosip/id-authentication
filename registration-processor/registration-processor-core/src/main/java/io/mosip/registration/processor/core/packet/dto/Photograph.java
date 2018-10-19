package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class Photograph {

	private String photographName;
	private boolean hasExceptionPhoto;
	private String exceptionPhotoName;
	private Double qualityScore;
	private Integer numRetry;

}