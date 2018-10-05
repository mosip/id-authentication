package io.mosip.authentication.service.impl.idauth.demo;

import java.util.Set;

import io.mosip.authentication.core.spi.indauth.demo.DemoDTOInfoFetcher;

public enum DemoMatchType {
	
  LANG1_NAME(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL), demo -> demo.getPersonalIdentityDTO().getNamePri(), entity->entity.getFirstName());
	
	private static Set<MatchingStrategy> allowedMatchingStrategy;
	private DemoDTOInfoFetcher demoInfo;
	private DemoEntityInfoFetcher entityInfo;
	
	private DemoMatchType(Set<MatchingStrategy> allowedMatchingStrategy, DemoDTOInfoFetcher demoInfo,
			DemoEntityInfoFetcher entityInfo) {
		allowedMatchingStrategy = allowedMatchingStrategy;
		this.demoInfo = demoInfo;
		this.entityInfo = entityInfo;
	}

	public Set<MatchingStrategy> getAllowedMatchingStrategy() {
		return allowedMatchingStrategy;
	}

	public DemoDTOInfoFetcher getDemoInfo() {
		return demoInfo;
	}

	public DemoEntityInfoFetcher getEntityInfo() {
		return entityInfo;
	}
	
	public static Set<MatchingStrategy> setOf(MatchingStrategy... matchingStrategies) {
		return allowedMatchingStrategy;
		
	}
	
	
}
