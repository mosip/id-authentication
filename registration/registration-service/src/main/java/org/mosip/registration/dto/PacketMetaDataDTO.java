package org.mosip.registration.dto;

import lombok.Data;

/**
 * 
 * @author M1047595
 *
 */
@Data
public class PacketMetaDataDTO {
	private double geoLatitudeLoc;
	private double geoLongitudeLoc;
	// New , update , correction, lost UIN
	private String applicationType;
	// Infant or Child, Regular
	private String applicationCategory;
	
}
