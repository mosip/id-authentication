package io.mosip.authentication.core.otp.dto;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.ObjectWithIdVersionTransactionID;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.indauth.dto.AuthError;
import lombok.Data;

/**
 * This class is used to provide response for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
public class OtpResponseDTO implements ObjectWithIdVersionTransactionID {

	/** Variable to hold id */
	private String id;

	/** Variable to hold id */
	private String version;

	/** Variable to hold id */
	private String transactionID;

	/** Variable to hold id */
	private String responseTime;

	/** List to hold errors */
	private List<AuthError> errors;

	/** List to hold response */
	private MaskedResponseDTO response;
	
}
