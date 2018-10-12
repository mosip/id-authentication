package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.service.impl.indauth.service.MatchType;

/**
 * @author Arun Bose The Enum DemoMatchType.
 */


public enum DemoMatchType implements MatchType{

	/** The addr pri. */
	ADDR_PRI(setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL),
			demo -> demo.getPersonalFullAddressDTO().getAddrPri(),
			entity -> entity.getAddrLine1() + " " + entity.getAddrLine2() + " "
					+ entity.getAddrLine3() + " "+entity.getNationalId()+" " + entity.getLocationCode(),		
			AuthUsageDataBit.USED_FAD_ADDR_PRI, AuthUsageDataBit.MATCHED_FAD_ADDR_PRI),

	NAME_PRI(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL),
			demo -> demo.getPersonalIdentityDTO().getNamePri(), 
			entity -> entity.getFirstName()+" "+entity.getMiddleName()+" "+entity.getLastName(),//FIXME for getting consolidated name as it requires admin config
			AuthUsageDataBit.USED_PI_NAME_PRI, AuthUsageDataBit.MATCHED_PI_NAME_PRI),
	
	
	/** The addr sec. */
	ADDR_SEC(setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL),
			demo -> demo.getPersonalFullAddressDTO().getAddrSec(),
			entity -> entity.getAddrLine1() + " " + entity.getAddrLine2() + " "
					+ entity.getAddrLine3() + " "+entity.getNationalId()+" " + entity.getLocationCode()
					, AuthUsageDataBit.USED_FAD_ADDR_SEC, AuthUsageDataBit.MATCHED_FAD_ADDR_SEC),
	
	/** The name sec. */
	NAME_SEC(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL),
			demo -> demo.getPersonalIdentityDTO().getNameSec(), 
			entity -> entity.getFirstName()+" "+entity.getMiddleName()+" "+entity.getLastName(),//FIXME for getting consolidated name as it requires admin config
			AuthUsageDataBit.USED_PI_NAME_SEC, AuthUsageDataBit.MATCHED_PI_NAME_SEC),
	
	/** The gender. */
	GENDER(setOf(GenderMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getGender(),
			entity ->entity.getGenderCode(),
			AuthUsageDataBit.USED_PI_GENDER, AuthUsageDataBit.MATCHED_PI_GENDER),

	/** The age. */
	AGE(setOf(AgeMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getAge(),
			entity ->entity.getAge(),
			AuthUsageDataBit.USED_PI_AGE, AuthUsageDataBit.MATCHED_PI_AGE),

	/** The dob. */
	DOB(setOf(DOBMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getDob(),
			entity ->entity.getDob(),
			AuthUsageDataBit.USED_PI_DOB, AuthUsageDataBit.MATCHED_PI_DOB),

	/** The mobile. */
	MOBILE(setOf(PhoneNoMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getPhone(),
			entity ->entity.getMobile(),
			AuthUsageDataBit.USED_PI_PHONE, AuthUsageDataBit.MATCHED_PI_PHONE),

	/** The email. */
	EMAIL(setOf(EmailMatchingStrategy.EXACT), demo -> demo.getPersonalIdentityDTO().getEmail(),
			entity ->entity.getEmail(),
			AuthUsageDataBit.USED_PI_EMAIL, AuthUsageDataBit.MATCHED_PI_EMAIL),

	/** The addr line1 pri. */
	ADDR_LINE1_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getAddrLine1Pri(),
			entity -> entity.getAddrLine1(),
			AuthUsageDataBit.USED_AD_ADDR_LINE1_PRI, AuthUsageDataBit.MATCHED_AD_ADDR_LINE1_PRI),

	/** The addr line2 pri. */
	ADDR_LINE2_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getAddrLine2Pri(),
			entity -> entity.getAddrLine2(),
			AuthUsageDataBit.USED_AD_ADDR_LINE2_PRI, AuthUsageDataBit.MATCHED_AD_ADDR_LINE2_PRI),

	/** The addr line3 pri. */
	ADDR_LINE3_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getAddrLine3Pri(),
			entity ->entity.getAddrLine3(),
			AuthUsageDataBit.USED_AD_ADDR_LINE3_PRI, AuthUsageDataBit.MATCHED_AD_ADDR_LINE3_PRI),

	/** The country pri. */
	COUNTRY_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getCountryPri(),
			entity ->entity.getNationalId(),
			AuthUsageDataBit.USED_AD_ADDR_COUNTRY_PRI, AuthUsageDataBit.MATCHED_AD_ADDR_COUNTRY_PRI),

	/** The pincode pri. */
	PINCODE_PRI(setOf(NameMatchingStrategy.EXACT), demo -> demo.getPersonalAddressDTO().getPinCodePri(),
			entity ->entity.getLocationCode(),
			AuthUsageDataBit.USED_AD_ADDR_PINCODE_PRI, AuthUsageDataBit.MATCHED_AD_ADDR_PINCODE_PRI);


	/** The allowed matching strategy. */
	private  Set<MatchingStrategy> allowedMatchingStrategy;
	
	/** The demo info. */
	private DemoDTOInfoFetcher demoInfo;
	
	/** The entity info. */
	private DemoEntityInfoFetcher entityInfo;

	/** The used bit. */
	private AuthUsageDataBit usedBit;

	/** The matched bit. */
	private AuthUsageDataBit matchedBit;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param demoInfo the demo info
	 * @param entityInfo the entity info
	 * @param usedBit the used bit
	 * @param matchedBit the matched bit
	 */
	private DemoMatchType(Set<MatchingStrategy> allowedMatchingStrategy, DemoDTOInfoFetcher demoInfo,
			DemoEntityInfoFetcher entityInfo, AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit) {
		this.allowedMatchingStrategy = allowedMatchingStrategy;
		this.demoInfo = demoInfo;
		this.entityInfo = entityInfo;
		this.usedBit = usedBit;
		this.matchedBit = matchedBit;
	}


	/**
	 * Gets the allowed matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @return the allowed matching strategy
	 */
	public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType) {
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
	 * Gets the used bit.
	 *
	 * @return the used bit
	 */
	public AuthUsageDataBit getUsedBit() {
		return usedBit;
	}
	
	/**
	 * Gets the matched bit.
	 *
	 * @return the matched bit
	 */
	public AuthUsageDataBit getMatchedBit() {
		return matchedBit;
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
