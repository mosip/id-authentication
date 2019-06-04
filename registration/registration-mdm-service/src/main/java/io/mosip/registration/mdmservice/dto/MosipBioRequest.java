package io.mosip.registration.mdmservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the Biometric capture request data
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class MosipBioRequest {

	private String type;
	private int count;
	private String format;
	private String requestedScore;
	private String deviceId;
	private String deviceSubId;
	private String previousHash;

}
