package io.mosip.authentication.core.spi.indauth.match;

import lombok.AllArgsConstructor;
import lombok.Data;





/**
 * The Class MatchOutput.
 *
 * @author Arun Bose
 * Instantiates a new match output.
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
	
	
}	
