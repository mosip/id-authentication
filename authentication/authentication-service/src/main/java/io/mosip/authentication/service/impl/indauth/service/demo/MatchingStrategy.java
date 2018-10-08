package io.mosip.authentication.service.impl.indauth.service.demo;

import io.mosip.authentication.service.impl.indauth.service.demo.MatchStrategyType;

public interface MatchingStrategy {

	MatchStrategyType getType();
	
	int getDefaultMatchValue();
	
	MatchFunction getMatchFunction();
	
}
