package io.mosip.authentication.core.otp.dto;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class is used to provide request for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class OtpRequestDTO extends BaseRequestDTO implements ObjectWithMetadata {

	private List<String> otpChannel;
	
	private Map<String, Object> metadata;

}