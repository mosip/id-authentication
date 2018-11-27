package io.mosip.authentication.service.impl.indauth.service.demo;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.service.impl.indauth.service.OTPAuthServiceImpl;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * @author Arun Bose The Enum DemoMatchType.
 */

public enum DemoMatchType implements MatchType {

	// @formatter:off

	/** Primary Name Match Type */
	NAME_PRI(IdMapping.NAME, setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL, NameMatchingStrategy.PHONETICS), IdentityDTO::getName,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_PI_NAME_PRI, AuthUsageDataBit.MATCHED_PI_NAME_PRI,
			Function.identity()),

	/** Secondary Name Match Type */
	NAME_SEC(IdMapping.NAME, setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL, NameMatchingStrategy.PHONETICS), IdentityDTO::getName,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_PI_NAME_SEC, AuthUsageDataBit.MATCHED_PI_NAME_SEC,
			Function.identity()),

	/** Secondary Date of Birth Match Type */
	DOB(IdMapping.DOB, setOf(DOBMatchingStrategy.EXACT), IdentityDTO::getDateOfBirth, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_DOB, AuthUsageDataBit.MATCHED_PI_DOB, Function.identity()),

	/** Secondary Date of Birth Type Match */
	DOBTYPE(IdMapping.DOBTYPE, setOf(DOBTypeMatchingStrategy.EXACT), IdentityDTO::getDateOfBirthType,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_PI_DOBTYPE, AuthUsageDataBit.MATCHED_PI_DOB_TYPE,
			Function.identity()),

	/** Secondary Date of Birth Type Match */
	AGE(IdMapping.AGE, setOf(AgeMatchingStrategy.EXACT), IdentityDTO::getAge, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_AGE, AuthUsageDataBit.MATCHED_PI_AGE, demoValue -> {
				int age = -1;
				try {
					age = Period.between(DOBMatchingStrategy.getDateFormat().parse(demoValue).toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
				} catch (ParseException e) {
					getLogger().error("sessionId", "IdType", "Id", e.getMessage());
				}
				return String.valueOf(Math.abs(age));
			}),

	/** Gender Match Type */
	GENDER(IdMapping.GENDER, setOf(GenderMatchingStrategy.EXACT), IdentityDTO::getGender, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_GENDER, AuthUsageDataBit.MATCHED_PI_GENDER, Function.identity()),
	/** Phone Match Type */
	PHONE(IdMapping.PHONE, setOf(PhoneNoMatchingStrategy.EXACT), IdentityDTO::getPhoneNumber, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_PHONE, AuthUsageDataBit.MATCHED_PI_PHONE, Function.identity()),
	/** E-mail Match Type */
	EMAIL(IdMapping.EMAIL, setOf(EmailMatchingStrategy.EXACT), IdentityDTO::getEmailId, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_EMAIL, AuthUsageDataBit.MATCHED_PI_EMAIL, Function.identity()),

	ADDR_LINE1_PRI(IdMapping.ADDRESSLINE1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine1,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE1_PRI,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE1_PRI, Function.identity()),

	/** The addr line1 pri. */
	ADDR_LINE1_SEC(IdMapping.ADDRESSLINE1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine1,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE1_SEC,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE1_SEC, Function.identity()),

	/** The addr line2 pri. */
	ADDR_LINE2_PRI(IdMapping.ADDRESSLINE2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine2,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE2_PRI,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE2_PRI, Function.identity()),

	/** The addr line2 pri. */
	ADDR_LINE2_SEC(IdMapping.ADDRESSLINE2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine2,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE2_SEC,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE2_SEC, Function.identity()),

	/** The addr line3 pri. */
	ADDR_LINE3_PRI(IdMapping.ADDRESSLINE3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine3,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE3_PRI,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE3_PRI, Function.identity()),

	/** The addr line3 pri. */
	ADDR_LINE3_SEC(IdMapping.ADDRESSLINE3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine3,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE3_SEC,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE3_SEC, Function.identity()),
	/** Location1 Match Type primary */
	LOCATION1_PRI(IdMapping.LOCATION1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation1,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_LOCATION1_PRI, AuthUsageDataBit.MATCHED_AD_LOCATION1_PRI,
			Function.identity()),
	/** Location1 Match Type secondary */
	LOCATION1_SEC(IdMapping.LOCATION1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation1,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_LOCATION1_SEC, AuthUsageDataBit.MATCHED_AD_LOCATION1_SEC,
			Function.identity()),
	/** Location2 Match Type primary */
	LOCATION2_PRI(IdMapping.LOCATION2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation2,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_LOCATION2_PRI, AuthUsageDataBit.MATCHED_AD_LOCATION2_PRI,
			Function.identity()),
	/** Location2 Match Type secondary */
	LOCATION2_SEC(IdMapping.LOCATION2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation2,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_LOCATION2_SEC, AuthUsageDataBit.MATCHED_AD_LOCATION2_SEC,
			Function.identity()),
	/** Location3 Match Type primary */
	LOCATION3_PRI(IdMapping.LOCATION3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation3,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_LOCATION3_PRI, AuthUsageDataBit.MATCHED_AD_LOCATION3_PRI,
			Function.identity()),
	/** Location3 Match Type secondary */
	LOCATION3_SEC(IdMapping.LOCATION3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation3,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_LOCATION3_SEC, AuthUsageDataBit.MATCHED_AD_LOCATION3_SEC,
			Function.identity()),

	/** The pincode pri. */
	PINCODE_PRI(IdMapping.PINCODE, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getPinCode,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_ADDR_PINCODE_PRI,
			AuthUsageDataBit.MATCHED_AD_ADDR_PINCODE_PRI, Function.identity()),

	/** The pincode pri. */
	PINCODE_SEC(IdMapping.PINCODE, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getPinCode,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_ADDR_PINCODE_SEC,
			AuthUsageDataBit.MATCHED_AD_ADDR_PINCODE_SEC, Function.identity()),

	/** Primary Address MatchType */
	ADDR_PRI(IdMapping.FULLADDRESS, setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL, FullAddressMatchingStrategy.PHONETICS),
			IdentityDTO::getFullAddress, LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_FAD_ADDR_PRI,
			AuthUsageDataBit.MATCHED_FAD_ADDR_PRI, Function.identity()),
	/** Secondary Address MatchType */
	ADDR_SEC(IdMapping.FULLADDRESS, setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL, FullAddressMatchingStrategy.PHONETICS),
			IdentityDTO::getFullAddress, LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_FAD_ADDR_SEC,
			AuthUsageDataBit.MATCHED_FAD_ADDR_SEC, Function.identity()),

	// @formatter:on
	;
	
	/** The mosipLogger. */
	private static final Logger mosipLogger = IdaLogger.getLogger(DemoMatchType.class);


	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The entity info. */
	private Function<String, String> entityInfoFetcher;

	/** The used bit. */
	private AuthUsageDataBit usedBit;

	/** The matched bit. */
	private AuthUsageDataBit matchedBit;

	private LanguageType langType;

	private Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction;

	private IdMapping idMapping;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param demoInfo                the demo info
	 * @param entityInfo              the entity info
	 * @param usedBit                 the used bit
	 * @param matchedBit              the matched bit
	 */
	DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction, LanguageType langType,
			AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit, Function<String, String> entityInfoFetcher) {
		this.idMapping = idMapping;
		this.identityInfoFunction = identityInfoFunction;
		this.langType = langType;
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
		this.entityInfoFetcher = entityInfoFetcher;
		this.usedBit = usedBit;
		this.matchedBit = matchedBit;
	}
	
	
	public LanguageType getLanguageType() {
		return langType;
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
	 * Gets the entity info.
	 *
	 * @return the entity info
	 */
	public Function<String, String> getEntityInfoFetcher() {
		return entityInfoFetcher;
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

	public IdMapping getIdMapping() {
		return idMapping;
	}


	@Override
	public Function<IdentityDTO, List<IdentityInfoDTO>> getIdentityInfoFunction() {
		return identityInfoFunction;
	}
	
	private static Logger getLogger() {
		return mosipLogger;
	}

}
