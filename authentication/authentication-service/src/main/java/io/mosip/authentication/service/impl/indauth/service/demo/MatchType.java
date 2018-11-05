package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;

/**
 *  Base interface for the match type
 * 
 * @authour Loganathan Sekar
 */
public interface MatchType {
	public IdMapping getIdMapping();
	
    Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType);
    
    public Function<IdentityDTO, List<IdentityInfoDTO>> getIdentityInfoFunction();
}
