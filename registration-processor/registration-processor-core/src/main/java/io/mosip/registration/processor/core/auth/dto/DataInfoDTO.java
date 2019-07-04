/**
 * 
 */
package io.mosip.registration.processor.core.auth.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author Ranjitha Siddegowda
 *
 */
@Data
public class DataInfoDTO {
	
	private String bioSubType;
	
	private String bioType;
	
	private String bioValue;
	
	private String deviceCode;
	
	private String deviceProviderID;
	
	private String deviceServiceID;
	
	private String deviceServiceVersion;
	
	private String environment;
	
	private String mosipProcess;
	
	private String timestamp;
	
	private String transactionID;
	
	private String version;

}
