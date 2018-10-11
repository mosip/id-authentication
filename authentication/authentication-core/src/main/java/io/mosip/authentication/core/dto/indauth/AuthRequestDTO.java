package io.mosip.authentication.core.dto.indauth;

import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.mosip.authentication.core.spi.idauth.demo.PersonalIdentityDataDTO;
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

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.id.notNull}")
	private String id;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.idType.notNull}")
	private String idType;

	/**
	 * The value of the field or property must be a number within a specified range.
	 * The {@code integer} element specifies the maximum integral digits for the
	 * number, and the {@code fraction} element specifies the maximum fractional
	 * digits for the number.
	 */
	@Digits(fraction = 1, integer = 1, message = "{mosip.ida.validation.message.AuthRequest.ver}")
	private String ver;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.muaCode.notNull}")
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(min = 10, message = "{mosip.ida.validation.message.AuthRequest.muaCode}")
	private String muaCode;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.txnId.notNull}")
	@Pattern(regexp = "^[A-Za-z0-9]{10}", message = "{mosip.ida.validation.message.AuthRequest.txnId}")
	private String txnID;

	// The value of the field or property must be a date or time in the past or present.
	@PastOrPresent(message = "{mosip.ida.validation.message.AuthRequest.date}")
	private String reqTime;

	//@Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "{mosip.ida.validation.message.AuthRequest.asaLicenseKey}")
	private String msaLicenseKey;

	private String hmac;
	
	private String data;
	
	private String signature;
	
	private AuthTypeDTO authType;
	
	private AuthSecureDTO key;
	
	private PersonalIdentityDataDTO personalDataDTO;
	
	private PinDTO pinDTO;//FIX IT
	
	
	
	

}
