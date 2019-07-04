package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Class MatchInput constructs the core match value of the  which has to be authorised.
 *
 * @author Arun Bose .
 */

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new match input.
 *
 * @param authType the auth type
 * @param matchType the match type
 * @param matchStrategyType the match strategy type
 * @param matchValue the match value
 * @param matchProperties the match properties
 * @param language the language
 */
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

	/** The match properties. */
	private Map<String, Object> matchProperties;

	/** The language. */
	private String language;

}
