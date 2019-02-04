package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;
import java.util.stream.Collectors;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Interface MatchingStrategy.
 * @author  Arun Bose
 */
public interface TextMatchingStrategy extends MatchingStrategy {

	public default int match(Map<String, String> reqValues, Map<String, String> entityValues, Map<String, Object> matchProperties) throws IdAuthenticationBusinessException {
		String reqInfo = reqValues.values().stream().collect(Collectors.joining(" "));
		String entityInfo = entityValues.values().stream().collect(Collectors.joining(" "));
		return  getMatchFunction().match(reqInfo, entityInfo, matchProperties);
	}
	
}
