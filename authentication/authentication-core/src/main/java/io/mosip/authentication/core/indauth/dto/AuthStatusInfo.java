package io.mosip.authentication.core.indauth.dto;

import java.util.List;

import lombok.Data;

/**
 * General-purpose of {@code AuthStatusInfo} class used to provide Auth status
 * Info
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class AuthStatusInfo {

	/** The status. */
	private boolean status;

	/** The err. */
	private List<AuthError> err;
}
