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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
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
	AGE(IdaIdMapping.DOB, setOf(AgeMatchingStrategy.EXACT), identityDTO -> getIdInfoList(identityDTO.getAge()), false,
			(entityInfoMap, props) -> {
				Optional<String> valueOpt = entityInfoMap.values().stream().findFirst();
				if (valueOpt.isPresent()) {
					String value = valueOpt.get();
					int age = Period.between(DateUtils.parseToDate(value, getDatePattern(props)).toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();

					Map<String, String> map = new LinkedHashMap<>();
					map.put(IdaIdMapping.AGE.getIdname(), String.valueOf(age));
					return map;
				} else {
					return Map.of();
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

	/** The addr line1. */
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

	/** The dynamic. */
	DYNAMIC(IdaIdMapping.DYNAMIC, setOf(DynamicDemoAttributeMatchingStrategy.EXACT)) {
		@Override
		public Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
			return req -> {
				if(req != null && req.getDemographics() != null && req.getDemographics().getMetadata() != null) {
					Map<String, Object> dynamicAttributes = req.getDemographics().getMetadata();
					return IdInfoFetcher.getIdInfo(dynamicAttributes);
				}
				return Map.of();
			};
		}
		
		@Override
		public boolean isDynamic() {
			return true;
		}
		
		public boolean isPropMultiLang(String propName, MappingConfig cfg) {
			return true;
		}
		
		@Override
		public boolean isMultiLanguage(String propName, Map<String, List<IdentityInfoDTO>> identityEntity,  MappingConfig mappingConfig) {
			Map<String, List<String>> dynamicAttributes = mappingConfig.getDynamicAttributes();
			List<String> propValues = dynamicAttributes.get(propName);
			List<IdentityInfoDTO> infoDtos;
			if(propValues != null && !propValues.isEmpty()) {
				//If mapping is there, use the mapping values to fetch the record
				infoDtos = identityEntity.entrySet()
						.stream()
						.filter(entry -> propValues.contains(entry.getKey()))
						.flatMap(entry -> entry.getValue().stream())
						.collect(Collectors.toList());
			} else {
				//Otherwise use the property name itself to fetch the record
				infoDtos = identityEntity.get(propName);
			}
			if (infoDtos == null || infoDtos.stream().anyMatch(infoDto -> infoDto.getLanguage() == null)) {
				return false;
			}
			
			return isMultiLanguage();
		}
	},
	
	/** The Constant DATE_PATTERN. */
	// @formatter:on
	;

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The entity info. */
	private BiFunction<Map<String, String>, Map<String, Object>,  Map<String, String>> entityInfoFetcher;
	
	/** The identity info function. */
	private Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> identityInfoFunction;

	/** The id mapping. */
	private IdMapping idMapping;

	/** The multi language. */
	private boolean multiLanguage;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param idMapping the id mapping
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param identityInfoFunction the identity info function
	 * @param multiLanguage the multi language
	 * @param entityInfoFetcher the entity info fetcher
	 */
	private DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction, boolean multiLanguage,
			BiFunction<Map<String, String>, Map<String, Object>, Map<String, String>> entityInfoFetcher) {
		this.idMapping = idMapping;
		this.identityInfoFunction = (RequestDTO identityDTO) -> {
			Map<String, List<IdentityInfoDTO>> map = new HashMap<>();
			if(identityDTO != null && identityDTO.getDemographics() != null) {
				map.put(idMapping.getIdname(), identityInfoFunction.apply(identityDTO.getDemographics()));
			}
			return map;
		};
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
		this.entityInfoFetcher = entityInfoFetcher;
		this.multiLanguage = multiLanguage;
	}

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param idMapping the id mapping
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param identityInfoFunction the identity info function
	 */
	private DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction) {
		this(idMapping, allowedMatchingStrategy, identityInfoFunction, true);
	}

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param idMapping the id mapping
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param identityInfoFunction the identity info function
	 * @param multiLanguage the multi language
	 */
	private DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction, boolean multiLanguage) {
		this(idMapping, allowedMatchingStrategy, identityInfoFunction, multiLanguage, (entity, prop) -> entity);
	}
	
	/**
	 * Instantiates a new demo match type.
	 *
	 * @param idMapping the id mapping
	 * @param allowedMatchingStrategy the allowed matching strategy
	 */
	private DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy) {
		this(idMapping, allowedMatchingStrategy, null, true, (entity, prop) -> entity);
	}
	
	/**
	 * Gets the date pattern.
	 * @param props 
	 *
	 * @return the date pattern
	 */
	private static String getDatePattern(Map<String, Object> props) {
		if(props != null) {
			Object object = props.get(IdInfoFetcher.class.getSimpleName());
			if(object instanceof IdInfoFetcher) {
				IdInfoFetcher idInfoFetcher = (IdInfoFetcher) object;
				return idInfoFetcher.getEnvironment().getProperty(IdAuthConfigKeyConstants.MOSIP_DATE_OF_BIRTH_PATTERN, IdAuthCommonConstants.DEFAULT_DOB_PATTERN);
			}
		}
		return IdAuthCommonConstants.DEFAULT_DOB_PATTERN;
	}

	/**
	 * Gets the id info list.
	 *
	 * @param value the value
	 * @return the id info list
	 */
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
	public BiFunction<Map<String, String>, Map<String, Object>, Map<String, String>> getEntityInfoMapper() {
		return entityInfoFetcher;
	}

	/**
	 * Gets the id mapping.
	 *
	 * @return the id mapping
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchType#
	 * getIdMapping()
	 */
	public IdMapping getIdMapping() {
		return idMapping;
	}

	/**
	 * Gets the identity info function.
	 *
	 * @return the identity info function
	 */
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

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchType#getCategory()
	 */
	@Override
	public Category getCategory() {
		return Category.DEMO;
	}

	/**
	 * Checks if is multi language.
	 *
	 * @return true, if is multi language
	 */
	@Override
	public boolean isMultiLanguage() {
		return multiLanguage;
	}
	
	/**
	 * Checks if is prop multi lang.
	 *
	 * @param propName the prop name
	 * @param cfg the cfg
	 * @return true, if is prop multi lang
	 */
	@Override
	public boolean isPropMultiLang(String propName, MappingConfig cfg) {
		DemoMatchType[] values = DemoMatchType.values();
		Optional<DemoMatchType> demoMatchType = Stream.of(values).filter(
				matchType ->  matchType.getIdMapping().getSubIdMappings().isEmpty()
						&& matchType
						.getIdMapping()
						.getMappingFunction()
						.apply(cfg, matchType)
						.contains(propName)
				)
		.findFirst();
		if(demoMatchType.isPresent()) {
			return demoMatchType.get().isMultiLanguage();
		}
		return MatchType.super.isPropMultiLang(propName, cfg);
	}

}
