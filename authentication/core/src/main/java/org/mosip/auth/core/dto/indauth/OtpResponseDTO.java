package org.mosip.auth.core.dto.indauth;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * This class is used to provide response for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
public class OtpResponseDTO {

	private String status;
	private List<AuthError> errorCode;
	private String txnID;
	private Date responseTime;
	//private AuthResponseInfo info;

}
