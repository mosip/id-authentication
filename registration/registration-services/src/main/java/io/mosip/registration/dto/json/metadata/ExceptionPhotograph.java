package io.mosip.registration.dto.json.metadata;

import lombok.Data;

/**
 * This class contains the attributes to be displayed for Exception Photograph object in
 * PacketMetaInfo JSON
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Data
public class ExceptionPhotograph {
	
	private String individualType;
	private String photoName;
	private int numRetry;
}
