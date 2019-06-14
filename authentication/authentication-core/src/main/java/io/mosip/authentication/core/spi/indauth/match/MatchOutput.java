package io.mosip.authentication.core.spi.indauth.match;

import lombok.AllArgsConstructor;
import lombok.Data;





/**
 * The Class MatchOutput is used to get the status of the authentication.
 *
 * @author Arun Bose
 */
@Data
@AllArgsConstructor
public class MatchOutput {

	/** The match value. */
	private int matchValue;
	
	/** The matched. */
	private boolean matched;
	
	/** The match strategy type. */
	private String matchStrategyType;
	
	/** The demo match type. */
	private MatchType matchType;
	
	/**  The language   */
	private String language;
	
	
}	
