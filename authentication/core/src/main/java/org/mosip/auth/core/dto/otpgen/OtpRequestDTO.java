package org.mosip.auth.core.dto.otpgen;

import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.mosip.auth.core.dto.indauth.IdType;

import lombok.Data;

/**
 * This class is used to provide request for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
//@NotNull
public class OtpRequestDTO  {

	private static final long serialVersionUID = 587362391440358285L;

	@Pattern(regexp = "^[0-9]{10}", message = "uniqueID validation failed")
	private String uniqueID;

//	@NotEmpty
	@NotNull
	private IdType idType;

	@Digits(fraction = 1, integer = 1)
	private String version;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String auaCode;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String txnID;

//	@DateTimeFormat(pattern = "dd/MM/YY")
	private Date requestTime;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String asaLicenseKey;

}