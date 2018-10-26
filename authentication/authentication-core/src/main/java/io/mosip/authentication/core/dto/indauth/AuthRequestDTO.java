package io.mosip.authentication.core.dto.indauth;

import java.util.List;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
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
public class AuthRequestDTO {

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.id.notNull}")
	private String id;

	/**
	 * The value of the field or property must be a number within a specified range.
	 * The {@code integer} element specifies the maximum integral digits for the
	 * number, and the {@code fraction} element specifies the maximum fractional
	 * digits for the number.
	 */
	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.ver.notNull}")
	@Digits(fraction = 1, integer = 1, message = "{mosip.ida.validation.message.AuthRequest.ver}")
	private String ver;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.indid.notNull}")
	private String indId;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.idType.notNull}")
	private String indIdType;

	private AuthTypeDTO authType;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.muaCode.notNull}")
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(min = 10, message = "{mosip.ida.validation.message.AuthRequest.muaCode}")
	private String muaCode;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.txnId.notNull}")
	@Pattern(regexp = "^[A-Za-z0-9]{10}", message = "{mosip.ida.validation.message.AuthRequest.txnId}")
	private String txnID;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.reqTime.notNull}")
	private String reqTime;

	@NotNull(message = "{mosip.ida.validation.message.AuthRequest.reqHmac.notNull}")
	private String reqHmac;

	private AuthSecureDTO key;

	private List<MatchInfo> matchInfo;

	private PinInfo pinInfo;

	private RequestDTO request;

}
