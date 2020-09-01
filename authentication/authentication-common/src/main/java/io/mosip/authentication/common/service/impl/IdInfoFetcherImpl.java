package io.mosip.authentication.common.service.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.util.BioMatcherUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.demoauth.DemoNormalizer;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MasterDataFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.TriFunctionWithBusinessException;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;

/**
 * Helper class to fetch identity values from request
 * 
 * @author Dinesh Karuppiah.T
 */
@Service
public class IdInfoFetcherImpl implements IdInfoFetcher {

	/** The OTPManager */
	@Autowired
	private OTPManager otpManager;

	@Autowired(required = false)
	private BioMatcherUtil bioMatcherUtil;

	/**
	 * The Master Data Manager
	 */
	@Autowired
	private MasterDataManager masterDataManager;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	@Autowired
	private DemoNormalizer demoNormalizer;

	@Override
	public DemoNormalizer getDemoNormalizer() {
		return demoNormalizer;
	}

	/**
	 * Fetch language code from properties
	 *
	 * @param langType
	 *            - the language code
	 * @return the language code
	 */
	@Override
	public String getLanguageCode(LanguageType langType) {
		if (langType == LanguageType.PRIMARY_LANG) {
			return environment.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE);
		} else {
			return environment.getProperty(IdAuthConfigKeyConstants.MOSIP_SECONDARY_LANGUAGE);
		}
	}

	/*
	 * getLanguageCode Fetch language Name based on language code
	 */
	@Override
	public Optional<String> getLanguageName(String languageCode) {
		String languagName = null;
		String key = null;
		if (languageCode != null) {
			key = IdAuthConfigKeyConstants.MOSIP_PHONETIC_LANG.concat(languageCode.toLowerCase()); // mosip.phonetic.lang.
			String property = environment.getProperty(key);
			if (property != null && !property.isEmpty()) {
				String[] split = property.split("-");
				languagName = split[0];
			}
		}
		return Optional.ofNullable(languagName);
	}

	/**
	 * Fetch Identity info based on Match type and Identity
	 *
	 * 
	 * @return Map
	 */
	@Override
	public Map<String, String> getIdentityRequestInfo(MatchType matchType, RequestDTO identity, String language) {
		return getInfo(matchType.getIdentityInfoFunction().apply(identity), language);
	}

	/**
	 * Fetch the Identity info based on Identity Info map and Language.
	 *
	 * @param idInfosMap
	 *            the id infos map
	 * @param languageForMatchType
	 *            the language for match type
	 * @return the info
	 */
	private Map<String, String> getInfo(Map<String, List<IdentityInfoDTO>> idInfosMap, String languageForMatchType) {
		if (idInfosMap != null && !idInfosMap.isEmpty()) {
			return idInfosMap.entrySet().parallelStream()

					.map(entry -> new SimpleEntry<String, String>(entry.getKey(),
							Optional.ofNullable(entry.getValue()).flatMap(value -> value.stream()
									.filter(idInfo -> checkLanguageType(languageForMatchType, idInfo.getLanguage()))
									.map(IdentityInfoDTO::getValue).findAny()).orElse("")))
					.filter(entry -> entry.getValue().length() > 0)
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}
		return Collections.emptyMap();
	}

	/**
	 * Check language type.
	 *
	 * @param languageFromInput
	 *            the language for match type
	 * @param languageFromEntity
	 *            the language from req
	 * @return true, if successful
	 */
	public boolean checkLanguageType(String languageFromInput, String languageFromEntity) {
		if (languageFromInput == null || languageFromEntity == null || languageFromEntity.isEmpty()
				|| languageFromEntity.equalsIgnoreCase("null")) {
			return languageFromInput == null
					|| getLanguageCode(LanguageType.PRIMARY_LANG).equalsIgnoreCase(languageFromInput);
		} else {
			return languageFromInput.equalsIgnoreCase(languageFromEntity);
		}
	}

	/*
	 * Get Validataed Otp Function
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher#
	 * getValidateOTPFunction()
	 */
	@Override
	public ValidateOtpFunction getValidateOTPFunction() {
		return otpManager::validateOtp;
	}

	/*
	 * To get the valid Cbeff for Entity Info
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher#getCbeffValues(
	 * java.util.Map, io.mosip.authentication.core.spi.bioauth.CbeffDocType,
	 * io.mosip.authentication.core.spi.indauth.match.MatchType)
	 */
	@Override
	public Map<String, Entry<String, List<IdentityInfoDTO>>> getCbeffValues(Map<String, List<IdentityInfoDTO>> idEntity,
		CbeffDocType[] types, MatchType matchType) throws IdAuthenticationBusinessException {
		Map<String, Entry<String, List<IdentityInfoDTO>>> cbeffValuesForTypes = new HashMap<>();
		for (CbeffDocType type : types) {
			cbeffValuesForTypes.putAll(getCbeffValuesForCbeffDocType(type, matchType, idEntity));
		}
		return cbeffValuesForTypes;
	}

	private Map<String, Entry<String, List<IdentityInfoDTO>>> getCbeffValuesForCbeffDocType(CbeffDocType type,
			MatchType matchType, Map<String, List<IdentityInfoDTO>> idEntity) throws IdAuthenticationBusinessException {
		Map<String, String> bdbBasedOnType;
		try {
			bdbBasedOnType = idEntity.entrySet().stream()
										.filter(entry -> entry.getKey().toLowerCase().startsWith(type.getName().toLowerCase()))
										.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().get(0).getValue()));
		} catch (Exception e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorMessage(), type.getName()));
		}
		return bdbBasedOnType.entrySet().stream()
				.collect(Collectors.toMap(Entry<String, String>::getKey, (Entry<String, String> entry) -> {
					IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
					identityInfoDTO.setValue(entry.getValue());
					List<IdentityInfoDTO> idenityList = new ArrayList<>(1);
					idenityList.add(identityInfoDTO);
					return new SimpleEntry<>(getNameForCbeffName(entry.getKey(), matchType), idenityList);
				}));
	}

	/**
	 * Fetch the identity value.
	 *
	 * @param name
	 *            the name
	 * @param languageForMatchType
	 *            the language for match type
	 * @param demoInfo
	 *            the demo info
	 * @return the identity value
	 */
	private Stream<String> getIdentityValue(String name, String languageForMatchType,
			Map<String, List<IdentityInfoDTO>> demoInfo) {
		List<IdentityInfoDTO> identityInfoList = demoInfo.get(name);
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream()
					.filter(idinfo -> checkLanguageType(languageForMatchType, idinfo.getLanguage()))
					.map(idInfo -> idInfo.getValue());
		}

		return Stream.empty();
	}

	/**
	 * Get the Cbeff Name mapped on ID Repo based on Ida Mapping
	 * 
	 * @param cbeffName
	 * @param matchType
	 * @return
	 */
	private String getNameForCbeffName(String cbeffName, MatchType matchType) {
		return Stream.of(IdaIdMapping.values()).filter(cfg -> matchType.getIdMapping().equals(cfg)
				|| matchType.getIdMapping().getSubIdMappings().contains(cfg)).map(cfg -> {
					String idname;
					Set<IdMapping> subIdMappings = matchType.getIdMapping().getSubIdMappings();
					if (!subIdMappings.isEmpty() && matchType instanceof BioMatchType) {
						idname = Stream.of(((BioMatchType) matchType).getMatchTypesForSubIdMappings(subIdMappings))
								.filter(bioMatchType -> bioMatchType.getIdMapping().getMappingFunction()
										.apply(idMappingConfig, bioMatchType).contains(cbeffName))
								.findFirst().map(MatchType::getIdMapping).map(IdMapping::getIdname)
								.orElse(cfg.getIdname());
					} else {
						idname = cfg.getIdname();
					}
					List<String> cbeffNames = cfg.getMappingFunction().apply(idMappingConfig, matchType);
					return new SimpleEntry<>(idname, cbeffNames);
				}).filter(entry -> entry.getValue().stream().anyMatch(v -> v.equalsIgnoreCase(cbeffName)))
				.map(Entry::getKey).findAny().orElse("");
	}

	@Override
	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public MasterDataFetcher getTitleFetcher() {
		return masterDataManager::fetchTitles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher#
	 * getMatchingThreshold(java.lang.String)
	 */
	@Override
	public Optional<Integer> getMatchingThreshold(String key) {
		Integer threshold = null;
		if (Objects.nonNull(key)) {
			String property = environment.getProperty(key);
			if (property != null && !property.isEmpty()) {
				threshold = Integer.parseInt(property);
			}
		}
		return Optional.ofNullable(threshold);
	}

	public Optional<String> getTypeForIdName(String idName, IdMapping[] idMappings) {
		return Stream.of(idMappings).filter(idmap -> {
			String thisId = idName.replaceAll("\\d", "");
			String thatId = idmap.getIdname().replace(IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER, "");
			return thisId.equalsIgnoreCase(thatId);
		}).map(IdMapping::getType).findFirst();
	}

	@Override
	public TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> getMatchFunction(
			AuthType authType) {
		final TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> defaultFunc = (
				arg1, arg2, arg3) -> (double) 0;
		if (authType instanceof BioAuthType) {
			return bioMatcherUtil::match;
		} else {
			return defaultFunc;
		}
	}

}
