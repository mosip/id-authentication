package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Class MatchInput.
 *
 * @author Arun Bose Instantiates a new match input.
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
	
	private DeviceInfo deviceInfo;
	
	private String language;

}
