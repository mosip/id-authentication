package io.mosip.authentication.demo.dto;

import lombok.Data;

/**
 * The Class DataDTO.
 * 
 * @author Sanjay Murali
 */
@Data
public class DataDTO {

	/** The Value for type */
	private String bioType;

	/** The Value for subType */
	private String bioSubType;

	/** The Value for bioValue */
	private String bioValue;

	/** The Value for deviceCode */
	private String deviceCode;

	/** The Value for deviceProviderID */
	private String deviceProviderID;

	/** The Value for deviceServiceID */
	private String deviceServiceID;

	/** The Value for deviceServiceVersion */
	private String deviceServiceVersion;

	/** The Value for transactionID */
	private String transactionID;

	/** The Value for time stamp */
	private String timestamp;

	/** The Value for mosipProcess */
	private String mosipProcess;

	/** The Value for environment */
	private String environment;

	/** The Value for version */
	private String version;

}
