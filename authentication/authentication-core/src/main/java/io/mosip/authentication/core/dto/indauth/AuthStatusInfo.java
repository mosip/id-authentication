package io.mosip.authentication.core.dto.indauth;

import java.util.List;

import lombok.Data;

/**
 * Instantiates a new auth status info.
 */
@Data
public class AuthStatusInfo {
	
	/** The status. */
	private boolean status;
	
	/** The match infos. */
	private List<MatchInfo> matchInfos;
	
	/** The bio infos. */
	private List<BioInfo> bioInfos;
	
	/** The usage data bits. */
	private List<AuthUsageDataBit> usageDataBits;
	
	/** The err. */
	private List<AuthError> err;
}
