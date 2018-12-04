package io.mosip.authentication.service.impl.indauth.service.demo;

import lombok.AllArgsConstructor;
import lombok.Data;





/**
 * @author Arun Bose
 * Instantiates a new match output.
 *
 * @param matchValue the match value
 * @param matched the matched
 * @param matchStrategyType the match strategy type
 * @param demoMatchType the demo match type
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
	private MatchType demoMatchType;
	
	
}	
