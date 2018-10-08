package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DemoMatchType {

	NAME_PRI(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL),
			demo -> demo.getPersonalIdentityDTO().getNamePri(), entity -> entity.getFirstName()),
  //FIX this 
	ADDR_PRI(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL),
			demo->demo.getPersonalFullAddressDTO().getAddrPri(), 
			entity -> {
				String fullAddress=entity.getAddressLine1()+" "+entity.getAddressLine2()+" "+entity.getAddressLine3()+
				           " "+entity.getLocationCode();//FIX ME for getting correct full address where state and city not given
		
				return fullAddress;
			});
	

	private static Set<MatchingStrategy> allowedMatchingStrategy;
	private DemoDTOInfoFetcher demoInfo;
	private DemoEntityInfoFetcher entityInfo;

	private DemoMatchType(Set<MatchingStrategy> allowedMatchingStrategy, DemoDTOInfoFetcher demoInfo,
			DemoEntityInfoFetcher entityInfo) {
		allowedMatchingStrategy = allowedMatchingStrategy;
		this.demoInfo = demoInfo;
		this.entityInfo = entityInfo;
	}


	public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchStrategyType matchStrategyType) {
		return allowedMatchingStrategy.stream().filter(ms -> ms.getType().equals(matchStrategyType)).findAny();
	}

	public DemoDTOInfoFetcher getDemoInfo() {
		return demoInfo;
	}

	public DemoEntityInfoFetcher getEntityInfo() {
		return entityInfo;
	}

	public static Set<MatchingStrategy> setOf(MatchingStrategy... matchingStrategies) {
		return Stream.of(matchingStrategies).collect(Collectors.toSet());

	}

}
