package io.mosip.authentication.core.dto.otpgen;

import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.mosip.authentication.core.dto.indauth.IdType;
import lombok.Data;

/**
 * This class is used to provide request for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
public class OtpRequestDTO  {

	private static final long serialVersionUID = 587362391440358285L;

	@Pattern(regexp = "^[0-9]{10}", message = "uniqueID validation failed")
	private String id;

//	@NotEmpty
	@NotNull
	private IdType idType;

	@Digits(fraction = 1, integer = 1)
	private String ver;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String muaCode;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String txnID;

//	@DateTimeFormat(pattern = "dd/MM/YY")
	private Date reqTime;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String msaLicenseKey;

}