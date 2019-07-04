package io.mosip.authentication.common.service.impl.match;

import static io.mosip.authentication.core.spi.indauth.match.MatchType.setOf;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Enum DemoMatchType.
 *
 * @author Arun Bose
 */

public enum DemoMatchType implements MatchType {

	// @formatter:off

	/** Primary Name Match Type. */
	NAME(IdaIdMapping.NAME,
			setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL, NameMatchingStrategy.PHONETICS),
			IdentityDTO::getName),

	/** Secondary Date of Birth Match Type. */
	DOB(IdaIdMapping.DOB, setOf(DOBMatchingStrategy.EXACT), identityDTO -> getIdInfoList(identityDTO.getDob()), false),

	/** Secondary Date of Birth Type Match. */
	DOBTYPE(IdaIdMapping.DOBTYPE, setOf(DOBTypeMatchingStrategy.EXACT), IdentityDTO::getDobType, false),

	/** Secondary Date of Birth Type Match. */
	AGE(IdaIdMapping.AGE, setOf(AgeMatchingStrategy.EXACT), identityDTO -> getIdInfoList(identityDTO.getAge()), false,
			entityInfoMap -> {
				Optional<String> valueOpt = entityInfoMap.values().stream().findFirst();
				if (valueOpt.isPresent()) {
					String value = valueOpt.get();
					int age = Period.between(DateUtils.parseToDate(value, getDatePattern()).toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();

					Map<String, String> map = new LinkedHashMap<>();
					map.put(IdaIdMapping.AGE.getIdname(), String.valueOf(age));
					return map;
				} else {
					return null;
				}
			}),

	/** Gender Match Type. */
	GENDER(IdaIdMapping.GENDER, setOf(GenderMatchingStrategy.EXACT), IdentityDTO::getGender),

	/** Phone Match Type. */
	PHONE(IdaIdMapping.PHONE, setOf(PhoneNoMatchingStrategy.EXACT),
			identityDTO -> getIdInfoList(identityDTO.getPhoneNumber()), false),

	/** E-mail Match Type. */
	EMAIL(IdaIdMapping.EMAIL, setOf(EmailMatchingStrategy.EXACT),
			identityDTO -> getIdInfoList(identityDTO.getEmailId()), false),

	/**  */
	ADDR_LINE1(IdaIdMapping.ADDRESSLINE1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine1),

	/** The addr line2 pri. */
	ADDR_LINE2(IdaIdMapping.ADDRESSLINE2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine2),

	/** The addr line3 pri. */
	ADDR_LINE3(IdaIdMapping.ADDRESSLINE3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine3),

	/** Location1 Match Type primary. */
	LOCATION1(IdaIdMapping.LOCATION1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation1),

	/** Location2 Match Type primary. */
	LOCATION2(IdaIdMapping.LOCATION2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation2),

	/** Location3 Match Type primary. */
	LOCATION3(IdaIdMapping.LOCATION3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation3),

	/** The pincode pri. */
	PINCODE(IdaIdMapping.PINCODE, setOf(AddressMatchingStrategy.EXACT),
			identityDTO -> getIdInfoList(identityDTO.getPostalCode()), false),

	/** Primary Address MatchType. */
	ADDR(IdaIdMapping.FULLADDRESS, setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL,
			FullAddressMatchingStrategy.PHONETICS), IdentityDTO::getFullAddress),

	/**  */
	// @formatter:on
	;

	private static final String DATE_PATTERN = "yyyy/MM/dd";

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The entity info. */
	private Function<Map<String, String>, Map<String, String>> entityInfoFetcher;

	/**  */
	private LanguageType langType;

	/**  */
	private Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> identityInfoFunction;

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
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction, boolean multiLanguage,
			Function<Map<String, String>, Map<String, String>> entityInfoFetcher) {
		this.idMapping = idMapping;
		this.identityInfoFunction = (RequestDTO identityDTO) -> {
			Map<String, List<IdentityInfoDTO>> map = new HashMap<>();
			if(identityDTO.getDemographics() != null) {
				map.put(idMapping.getIdname(), identityInfoFunction.apply(identityDTO.getDemographics()));
			}
			return map;
		};
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
		this.entityInfoFetcher = entityInfoFetcher;
		this.multiLanguage = multiLanguage;
	}

	private static String getDatePattern() {
		// FIXME get from env.
		return DATE_PATTERN;
	}

	private static List<IdentityInfoDTO> getIdInfoList(String value) {
		if (value != null) {
			IdentityInfoDTO identityDTOs = new IdentityInfoDTO();
			identityDTOs.setValue(value);
			List<IdentityInfoDTO> list = new ArrayList<>();
			list.add(identityDTOs);
			return list;
		} else {
			return Collections.emptyList();
		}
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
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction) {
		this(idMapping, allowedMatchingStrategy, identityInfoFunction, true);
	}

	private DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction, boolean multiLanguage) {
		this(idMapping, allowedMatchingStrategy, identityInfoFunction, multiLanguage, Function.identity());
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
	public Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
		return identityInfoFunction;
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
	
	@Override
	public boolean isPropMultiLang(String propName, MappingConfig cfg) {
		DemoMatchType[] values = DemoMatchType.values();
		Optional<DemoMatchType> demoMatchType = Stream.of(values).filter(
				matchType ->  matchType.getIdMapping().getSubIdMappings().isEmpty()
						&& matchType.getIdMapping().getMappingFunction().apply(cfg, matchType).contains(propName)
				)
		.findFirst();
		if(demoMatchType.isPresent()) {
			return demoMatchType.get().isMultiLanguage();
		}
		return MatchType.super.isPropMultiLang(propName, cfg);
	}

}
