package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Arun Bose The Enum DemoMatchType.
 */


public enum DemoMatchType {
	
	
	
	

	/** The addr pri. */
	// FIX this
	ADDR_PRI(setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL),
			demo -> demo.getPersonalFullAddressDTO().getAddrPri(),
			entity -> entity.getAddrLine1() + " " + entity.getAddrLine2() + " "
					+ entity.getAddrLine3() + " "+entity.getNationalId()+" " + entity.getLocationCode() //FIXME for getting 
					

	),

	NAME_PRI(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL),
			demo -> demo.getPersonalIdentityDTO().getNamePri(), entity -> entity.getFirstName()+" "+entity.getMiddleName()+" "+entity.getLastName()),//FIXME for getting consolidated name as it requires admin config

	/** The gender. */
	GENDER(setOf(GenderMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getGender(),
			entity -> entity.getGenderCode()),

	/** The age. */
	AGE(setOf(AgeMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getAge(),
			entity -> entity.getAge()),

	/** The dob. */
	DOB(setOf(DOBMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getDob(),
			entity -> entity.getDob()),

	/** The mobile. */
	MOBILE(setOf(PhoneNoMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getPhone(),
			entity -> entity.getMobile()),

	/** The email. */
	EMAIL(setOf(EmailMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getEmail(),
			entity -> entity.getEmail()),

	/** The addr line1 pri. */
	ADDR_LINE1_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getAddrLine1Pri(),
			entity -> entity.getAddrLine1()),

	/** The addr line2 pri. */
	ADDR_LINE2_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getAddrLine2Pri(),
			entity -> entity.getAddrLine2()),

	/** The addr line3 pri. */
	ADDR_LINE3_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getAddrLine3Pri(),
			entity -> entity.getAddrLine3()),

	/** The country pri. */
	COUNTRY_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getCountryPri(),
			entity -> entity.getNationalId()),

	/** The pincode pri. */
	PINCODE_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getPinCodePri(),
			entity -> entity.getLocationCode());

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The demo info. */
	private DemoDTOInfoFetcher demoInfo;

	/** The entity info. */
	private DemoEntityInfoFetcher entityInfo;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param allowedMatchingStrategy
	 *            the allowed matching strategy
	 * @param demoInfo
	 *            the demo info
	 * @param entityInfo
	 *            the entity info
	 */
	private DemoMatchType(Set<MatchingStrategy> allowedMatchingStrategy, DemoDTOInfoFetcher demoInfo,
			DemoEntityInfoFetcher entityInfo) {
		this.allowedMatchingStrategy = allowedMatchingStrategy;
		this.demoInfo = demoInfo;
		this.entityInfo = entityInfo;
	}

	
	private DemoEntity primaryEntity;

	private DemoEntity SecondaryEntity;
	
	/**
	 * Gets the allowed matching strategy.
	 *
	 * @param matchStrategyType
	 *            the match strategy type
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
	public DemoDTOInfoFetcher getDemoInfoFetcher() {
		return demoInfo;
	}

	/**
	 * Gets the entity info.
	 *
	 * @return the entity info
	 */
	public DemoEntityInfoFetcher getEntityInfoFetcher() {
		return entityInfo;
	}

	/**
	 * Sets the of.
	 *
	 * @param matchingStrategies
	 *            the matching strategies
	 * @return the sets the
	 */
	public static Set<MatchingStrategy> setOf(MatchingStrategy... matchingStrategies) {
		return Stream.of(matchingStrategies).collect(Collectors.toSet());

	}
	
	

}
