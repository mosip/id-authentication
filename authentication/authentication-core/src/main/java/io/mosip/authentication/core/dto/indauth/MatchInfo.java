package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Match Info class.
 *
 * @author Loganathan Sekaran
 */

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new match info.
 *
 * @param authType the auth type
 * @param ms the ms
 * @param mt the mt
 */
@AllArgsConstructor
public class MatchInfo {
	
	/** The auth type. */
	private String authType;
	
	/** The ms. */
	private String ms;
	
	/** The mt. */
	private Integer mt;
}
