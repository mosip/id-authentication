package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Arun Bose Instantiates a new match input.
 *
 * @param demoMatchType     the demo match type
 * @param matchStrategyType the match strategy type
 * @param matchValue        the match value
 */
@Data
@AllArgsConstructor
public class MatchInput {
	
	/** The match type. */
	private AuthType authType;

	/** The match type. */
	private MatchType matchType;

	/** The match strategy type. */
	private String matchStrategyType;

	/** The match value. */
	private Integer matchValue;

	private Map<String, Object> matchProperties;

}
