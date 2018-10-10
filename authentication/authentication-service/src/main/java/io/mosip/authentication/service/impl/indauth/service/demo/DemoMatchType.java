package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Arun Bose
 * The Enum DemoMatchType.
 */
public enum DemoMatchType {

	
  /** The addr pri. */
  //FIX this 
	ADDR_PRI(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL),
			demo->demo.getPersonalFullAddressDTO().getAddrPri(), 
			entity -> {
				String fullAddress=entity.getLang1AddressLine1()+" "+entity.getLang1AddressLine2()+" "+entity.getLang1AddressLine3()+
				           " "+entity.getLang1PinCode();//FIX ME for getting correct full address where state and city not given
		
				/** The name pri. */
return fullAddress;
			}),
	
	NAME_PRI(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL),
			demo -> demo.getPersonalIdentityDTO().getNamePri(), entity -> entity.getLang1Name()),
	
	/** The gender. */
	//FIXME THIS All enums for matching strategy
	GENDER(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalIdentityDTO().getGender(), 
			entity ->entity.getLang1Gender()),
	
	/** The age. */
	AGE(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalIdentityDTO().getAge(), 
			entity ->entity.getLang1Age()),
	
	/** The dob. */
	DOB(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalIdentityDTO().getDob(), 
			entity ->entity.getLang1Dob()),
	
	/** The dob type. */
	DOB_TYPE(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalIdentityDTO().getDobType(), 
			entity ->entity.getLang1DobType()),
	
	/** The mobile. */
	MOBILE(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalIdentityDTO().getPhone(), 
			entity ->entity.getLang1Mobile()),
	
	/** The email. */
	EMAIL(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalIdentityDTO().getEmail(), 
			entity ->entity.getLang1Email()),
	
	/** The addr line1 pri. */
	ADDR_LINE1_PRI(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalAddressDTO().getAddrLine(), 
			entity ->entity.getLang1Email()),
	
	/** The addr line2 pri. */
	ADDR_LINE2_PRI(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalAddressDTO().getAddrLine1(), 
			entity ->entity.getLang1Email()),
	
	/** The addr line3 pri. */
	ADDR_LINE3_PRI(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalAddressDTO().getAddrLine2(), 
			entity ->entity.getLang1Email()),
	
	/** The country pri. */
	COUNTRY_PRI(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalAddressDTO().getCountry(), 
			entity ->entity.getLang1Email()),
	
	/** The pincode pri. */
	PINCODE_PRI(setOf(NameMatchingStrategy.EXACT),
			demo->demo.getPersonalAddressDTO().getPinCode(), 
			entity ->entity.getLang1Email());
	

	/** The allowed matching strategy. */
	private  Set<MatchingStrategy> allowedMatchingStrategy;
	
	/** The demo info. */
	private DemoDTOInfoFetcher demoInfo;
	
	/** The entity info. */
	private DemoEntityInfoFetcher entityInfo;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param demoInfo the demo info
	 * @param entityInfo the entity info
	 */
	private DemoMatchType(Set<MatchingStrategy> allowedMatchingStrategy, DemoDTOInfoFetcher demoInfo,
			DemoEntityInfoFetcher entityInfo) {
		this.allowedMatchingStrategy = allowedMatchingStrategy;
		this.demoInfo = demoInfo;
		this.entityInfo = entityInfo;
	}


	/**
	 * Gets the allowed matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @return the allowed matching strategy
	 */
	public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchStrategyType matchStrategyType) {
		return allowedMatchingStrategy.stream().filter(ms -> ms.getType().equals(matchStrategyType)).findAny();
	}

	/**
	 * Gets the demo info.
	 *
	 * @return the demo info
	 */
	public DemoDTOInfoFetcher getDemoInfo() {
		return demoInfo;
	}

	/**
	 * Gets the entity info.
	 *
	 * @return the entity info
	 */
	public DemoEntityInfoFetcher getEntityInfo() {
		return entityInfo;
	}

	/**
	 * Sets the of.
	 *
	 * @param matchingStrategies the matching strategies
	 * @return the sets the
	 */
	public static Set<MatchingStrategy> setOf(MatchingStrategy... matchingStrategies) {
		return Stream.of(matchingStrategies).collect(Collectors.toSet());

	}

}
