package io.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class Photograph {

	private String photographName;
	private boolean hasExceptionPhoto;
	private String exceptionPhotoName;
	private double qualityScore;
	private int numRetry;
}