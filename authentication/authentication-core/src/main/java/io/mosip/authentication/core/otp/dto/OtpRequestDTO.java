package io.mosip.authentication.core.otp.dto;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import lombok.Data;

/**
 * This class is used to provide request for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
public class OtpRequestDTO implements ObjectWithMetadata{

	/** Variable to hold id */
	private String id;

	/** Variable to hold version */
	private String version;

	/** Variable to hold Transaction ID */
	private String transactionID;

	/** Variable to hold Request time */
	private String requestTime;

	/** Variable to hold individualID */
	private String individualId;

	/** Variable to hold partnerID */
	private String individualIdType;

	private List<String> otpChannel;
	
	private Map<String, Object> metadata;

}