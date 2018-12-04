package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Map;

import io.mosip.authentication.service.impl.indauth.builder.AuthType;
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
	
	/** The demo match type. */
	private AuthType authType;

	/** The demo match type. */
	private MatchType demoMatchType;

	/** The match strategy type. */
	private String matchStrategyType;

	/** The match value. */
	private Integer matchValue;

	private Map<String, Object> matchProperties;

}
