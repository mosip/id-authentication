package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;

/**
 *  Base interface for the match type
 * 
 * @authour Loganathan Sekar
 */
public interface MatchType {
    Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType);
}
