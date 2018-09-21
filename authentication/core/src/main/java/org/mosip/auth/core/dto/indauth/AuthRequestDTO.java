package org.mosip.auth.core.dto.indauth;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * General-purpose of {@code AuthRequestDTO} class used to communicate with
 * core-kernel API. This class is picked request and send to core-kernel. Where
 * core-kernal API processed operation with {@code AuthRequestDTO} attributes.
 * 
 * @author Rakesh Roshan
 */
@Data
public class AuthRequestDTO implements Serializable {

	private static final long serialVersionUID = 6239182261277493008L;

	@Pattern(regexp = "^[0-9]{10}$", message = "UniqeID size must be 10")
	@NotNull
	private String uniqueID;

	@NotNull
	private IDType idType;

	/**
	 * The value of the field or property must be a number within a specified range.
	 * The {@code integer} element specifies the maximum integral digits for the
	 * number, and the {@code fraction} element specifies the maximum fractional
	 * digits for the number.
	 */
	@Digits(fraction = 1, integer = 1, message = "version range max integer 1 digit and max fraction 1 digit only")
	private String version;

	@NotNull
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(min = 10, message = "auaCode size should be min 10")
	private String auaCode;

	@NotNull
	@Pattern(regexp = "^[A-Za-z0-9]{10}", message = "txnID size must be 10")
	private String txnID;

	@PastOrPresent(message = "Date should be past or present date")
	private Date requestTime;

	@Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "asaLicenseKey size must be 10")
	private String asaLicenseKey;

	
	private String hmac;
	
	private String data;
	
	private String signature;
	
	private AuthTypeDTO authType;
	
	private AuthSecureDTO key;
	
	private PinDTO pinDTO;
	

}
