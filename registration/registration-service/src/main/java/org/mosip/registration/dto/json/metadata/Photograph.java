package org.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class Photograph {

	private String photographName;
	private boolean hasExceptionPhoto;
	private String exceptionPhotoName;
	
}