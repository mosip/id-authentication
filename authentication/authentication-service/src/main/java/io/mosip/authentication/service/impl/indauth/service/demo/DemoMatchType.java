package io.mosip.authentication.service.impl.indauth.service.demo;

import static io.mosip.authentication.core.spi.indauth.match.MatchType.setOf;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Enum DemoMatchType.
 *
 * @author Arun Bose
 */

public enum DemoMatchType implements MatchType {

	// @formatter:off

	/** Primary Name Match Type. */
	NAME(IdaIdMapping.FULLNAME,
			setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL, NameMatchingStrategy.PHONETICS),
			IdentityDTO::getFullName, AuthUsageDataBit.USED_PI_NAME,
			AuthUsageDataBit.MATCHED_PI_NAME),

	/** Secondary Date of Birth Match Type. */
	DOB(IdaIdMapping.DOB, setOf(DOBMatchingStrategy.EXACT), IdentityDTO::getDob,
			AuthUsageDataBit.USED_PI_DOB, AuthUsageDataBit.MATCHED_PI_DOB),

	/** Secondary Date of Birth Type Match. */
	DOBTYPE(IdaIdMapping.DOBTYPE, setOf(DOBTypeMatchingStrategy.EXACT), IdentityDTO::getDobType,
			AuthUsageDataBit.USED_PI_DOBTYPE, AuthUsageDataBit.MATCHED_PI_DOB_TYPE),

	/** Secondary Date of Birth Type Match. */
	AGE(IdaIdMapping.AGE, setOf(AgeMatchingStrategy.EXACT), IdentityDTO::getAge,
			AuthUsageDataBit.USED_PI_AGE, AuthUsageDataBit.MATCHED_PI_AGE, false, entityInfoMap -> {
				int age = -1;
				try {
					String value = entityInfoMap.values().stream().findFirst().orElse("");
					age = Period.between(DOBMatchingStrategy.getDateFormat().parse(value).toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
				} catch (ParseException e) {
					getLogger().error("sessionId", "IdType", "Id", e.getMessage());
				}
				Map<String, String> map = new LinkedHashMap<>();
				map.put(IdaIdMapping.AGE.getIdname(), String.valueOf(age));
				return map;
			}),

	/** Gender Match Type. */
	GENDER(IdaIdMapping.GENDER, setOf(GenderMatchingStrategy.EXACT), IdentityDTO::getGender,
			AuthUsageDataBit.USED_PI_GENDER, AuthUsageDataBit.MATCHED_PI_GENDER, false),

	/** Phone Match Type. */
	PHONE(IdaIdMapping.PHONE, setOf(PhoneNoMatchingStrategy.EXACT), IdentityDTO::getPhoneNumber,
			AuthUsageDataBit.USED_PI_PHONE, AuthUsageDataBit.MATCHED_PI_PHONE, false),

	/** E-mail Match Type. */
	EMAIL(IdaIdMapping.EMAIL, setOf(EmailMatchingStrategy.EXACT), IdentityDTO::getEmailId,
			AuthUsageDataBit.USED_PI_EMAIL, AuthUsageDataBit.MATCHED_PI_EMAIL, false),

	/**  */
	ADDR_LINE1(IdaIdMapping.ADDRESSLINE1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine1,
			AuthUsageDataBit.USED_AD_ADDR_LINE1, AuthUsageDataBit.MATCHED_AD_ADDR_LINE1),

	/** The addr line2 pri. */
	ADDR_LINE2(IdaIdMapping.ADDRESSLINE2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine2,
			AuthUsageDataBit.USED_AD_ADDR_LINE2, AuthUsageDataBit.MATCHED_AD_ADDR_LINE2),

	/** The addr line3 pri. */
	ADDR_LINE3(IdaIdMapping.ADDRESSLINE3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine3,
			AuthUsageDataBit.USED_AD_ADDR_LINE3,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE3),

	/** Location1 Match Type primary. */
	LOCATION1(IdaIdMapping.LOCATION1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation1,
			AuthUsageDataBit.USED_AD_LOCATION1,
			AuthUsageDataBit.MATCHED_AD_LOCATION1),

	/** Location2 Match Type primary. */
	LOCATION2(IdaIdMapping.LOCATION2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation2,
			AuthUsageDataBit.USED_AD_LOCATION2,
			AuthUsageDataBit.MATCHED_AD_LOCATION2),

	/** Location3 Match Type primary. */
	LOCATION3(IdaIdMapping.LOCATION3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation3,
			AuthUsageDataBit.USED_AD_LOCATION3,
			AuthUsageDataBit.MATCHED_AD_LOCATION3),

	/** The pincode pri. */
	PINCODE(IdaIdMapping.PINCODE, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getPinCode,
			AuthUsageDataBit.USED_AD_ADDR_PINCODE,
			AuthUsageDataBit.MATCHED_AD_ADDR_PINCODE),

	/** Primary Address MatchType. */
	ADDR(IdaIdMapping.FULLADDRESS,
			setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL,
					FullAddressMatchingStrategy.PHONETICS),
			IdentityDTO::getFullAddress, AuthUsageDataBit.USED_FAD_ADDR,
			AuthUsageDataBit.MATCHED_FAD_ADDR),

	/**  */
	// @formatter:on
	;

	/** The mosipLogger. */
	private static final Logger mosipLogger = IdaLogger.getLogger(DemoMatchType.class);

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The entity info. */
	private Function<Map<String, String>, Map<String, String>> entityInfoFetcher;

	/** The used bit. */
	private AuthUsageDataBit usedBit;

	/** The matched bit. */
	private AuthUsageDataBit matchedBit;

	/**  */
	private LanguageType langType;

	/**  */
	private Function<IdentityDTO, Map<String, List<IdentityInfoDTO>>> identityInfoFunction;

	/**  */
	private IdMapping idMapping;
	
	private boolean multiLanguage;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param idMapping
	 * @param allowedMatchingStrategy
	 * @param identityInfoFunction
	 * @param langType
	 * @param usedBit
	 * @param matchedBit
	 * @param entityInfoFetcher
	 */
	private DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction,
			AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit, boolean multiLanguage,
			Function<Map<String, String>, Map<String, String>> entityInfoFetcher) {
		this.idMapping = idMapping;
		this.identityInfoFunction = (IdentityDTO identityDTO) -> {
			Map<String, List<IdentityInfoDTO>> map = new HashMap<>();
			map.put(idMapping.getIdname(), identityInfoFunction.apply(identityDTO));
			return map;
		};
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
		this.entityInfoFetcher = entityInfoFetcher;
		this.usedBit = usedBit;
		this.matchedBit = matchedBit;
		this.multiLanguage = multiLanguage;
	}

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param idMapping
	 * @param allowedMatchingStrategy
	 * @param identityInfoFunction
	 * @param langType
	 * @param usedBit
	 * @param matchedBit
	 */
	private DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction,
			AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit) {
		this(idMapping, allowedMatchingStrategy, identityInfoFunction, usedBit, matchedBit, true);
	}
	
	private DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction,
			AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit, boolean multiLanguage) {
		this(idMapping, allowedMatchingStrategy, identityInfoFunction, usedBit, matchedBit,multiLanguage,
				Function.identity());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.MatchType#getLanguageType()
	 */
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
	public Function<Map<String, String>, Map<String, String>> getEntityInfoMapper() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchType#
	 * getIdMapping()
	 */
	public IdMapping getIdMapping() {
		return idMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchType#
	 * getIdentityInfoFunction()
	 */
	@Override
	public Function<IdentityDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
		return identityInfoFunction;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	private static Logger getLogger() {
		return mosipLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchType#getCategory()
	 */
	@Override
	public Category getCategory() {
		return Category.DEMO;
	}
	
	@Override
	public boolean isMultiLanguage() {
		return multiLanguage;
	}

}
