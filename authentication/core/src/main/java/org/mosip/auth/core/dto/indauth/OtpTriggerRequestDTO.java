package org.mosip.auth.core.dto.indauth;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * This class is used to provide request for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
@NotNull
public class OtpTriggerRequestDTO implements Serializable {

	private static final long serialVersionUID = 587362391440358285L;

	

	@Pattern(regexp = "^[0-9] {10}", message = "uniqueID validation failed")
	private String uniqueID;

	// @EnumValidator
	private IDType idType;

	// @EnumValidator(versionForIDType = "2.0", enumClass = IDType.class, values = {
	// "VID" })
	@Digits(fraction = 1, integer = 1)
	private String version;

	@Pattern(regexp = "^[A-Z0-9] *")
	private String auaCode;

	@Pattern(regexp = "^[A-Z0-9] {10}")
	private String txnID;

	@DateTimeFormat(pattern = "dd/MM/YY")
	private Date requestTime;

	@Pattern(regexp = "^[A-Z0-9] {10}")
	private String asaLicenseKey;
	
	//@Transient
	private AuthTypeDTO requestedAuth;

}