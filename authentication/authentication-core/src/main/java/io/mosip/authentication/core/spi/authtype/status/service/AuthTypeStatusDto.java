package io.mosip.authentication.core.spi.authtype.status.service;

import java.util.List;

import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import lombok.Data;

/**
 * The Class AuthTypeStatusDto.
 *
 * @author Dinesh K
 */
@Data
public class AuthTypeStatusDto {

	/** The id. */
	private String id;
	
	/** The version. */
	private String version;
	
	/** The request time. */
	private String requestTime;
	
	/** The consent obtained. */
	private boolean consentObtained;
	
	/** The individual id. */
	private String individualId;
	
	/** The individual id type. */
	private String individualIdType;
	
	/** The request. */
	private List<AuthtypeStatus> request;

}
