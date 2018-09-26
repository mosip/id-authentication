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
public class AuthRequestDTO  {

	private static final long serialVersionUID = 6239182261277493008L;

	@Pattern(regexp = "^[0-9]{10}$", message = "{mosip.ida.validation.message.authRequest.uniqueId}")
	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.uniqueId.notNull}")
	private String uniqueID;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.idType.notNull}")
	private IDType idType;

	/**
	 * The value of the field or property must be a number within a specified range.
	 * The {@code integer} element specifies the maximum integral digits for the
	 * number, and the {@code fraction} element specifies the maximum fractional
	 * digits for the number.
	 */
	@Digits(fraction = 1, integer = 1, message = "{mosip.ida.validation.message.AuthRequest.version}")
	private String version;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.auaCode.notNull}")
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(min = 10, message = "{mosip.ida.validation.message.AuthRequest.auaCode}")
	private String auaCode;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.txnId.notNull}")
	@Pattern(regexp = "^[A-Za-z0-9]{10}", message = "{mosip.ida.validation.message.AuthRequest.txnId}")
	private String txnID;

	@PastOrPresent(message = "{mosip.ida.validation.message.AuthRequest.date}")
	private Date requestTime;

	//@Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "{mosip.ida.validation.message.AuthRequest.asaLicenseKey}")
	private String asaLicenseKey;

	
	private String hmac;
	
	private String data;
	
	private String signature;
	
	private AuthTypeDTO authType;
	
	private AuthSecureDTO key;
	
	private PinDTO pinDTO;
	

}
