package io.mosip.authentication.service.impl.idauth.demo;

import io.mosip.authentication.service.impl.idauth.demo.MatchStrategyType;

public interface MatchingStrategy {

	MatchStrategyType getType();
	
	int getDefaultMatchValue();
	
	MatchFunction getMatchFunction();
	
}
