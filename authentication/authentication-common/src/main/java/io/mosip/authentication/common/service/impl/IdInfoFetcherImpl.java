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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.common.service.impl.match.KeyBindedTokenAuthType;
import io.mosip.authentication.common.service.util.KeyBindedTokenMatcherUtil;
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
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MasterDataFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.TriFunctionWithBusinessException;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.authentication.core.util.DemoNormalizer;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BDBInfo;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.spi.CbeffUtil;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;

/**
 * Helper class to fetch identity values from request.
 *
 * @author Dinesh Karuppiah.T
 * @author Nagarjuna
 */
@Service
public class IdInfoFetcherImpl implements IdInfoFetcher {
	
	/**  The OTPManager. */
	@Autowired
	private OTPManager otpManager;

	/** The Cbeff Util. */
	@Autowired
	private CbeffUtil cbeffUtil;

	/** The bio matcher util. */
	@Autowired(required = false)
	private BioMatcherUtil bioMatcherUtil;

	/** The Master Data Manager. */
	@Autowired
	private MasterDataManager masterDataManager;

	/** The environment. */
	@Autowired
	private EnvUtil environment;

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The demo normalizer. */
	@Autowired(required = false)
	private DemoNormalizer demoNormalizer;
	
	/** The demo matecher util. */
	@Autowired(required = false)
	private DemoMatcherUtil demoMatcherUtil;

	@Autowired(required = false)
	private KeyBindedTokenMatcherUtil keyBindedTokenMatcherUtil;
	
	/**
	 * Gets the demo normalizer.
	 *
	 * @return the demo normalizer
	 */
	@Override
	public DemoNormalizer getDemoNormalizer() {
		return demoNormalizer;
	}

	/**
	 * Gets the language name.
	 *
	 * @param languageCode the language code
	 * @return the language name
	 */
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
	 * Gets the identity request info.
	 *
	 * @param matchType the match type
	 * @param identity the identity
	 * @param language the language
	 * @return the identity request info
	 */
	@Override
	public Map<String, String> getIdentityRequestInfo(MatchType matchType, RequestDTO identity, String language) {
		return getIdentityRequestInfo(matchType, matchType.getIdMapping().getIdname(), identity, language);
	}

	/**
	 * Fetch Identity info based on Match type and Identity.
	 *
	 * @param matchType the match type
	 * @param idName the id name
	 * @param identity the identity
	 * @param language the language
	 * @return Map
	 */
	@Override
	public Map<String, String> getIdentityRequestInfo(MatchType matchType, String idName, RequestDTO identity, String language) {
		Map<String, List<IdentityInfoDTO>> identityInfos = getIdentityInfo(matchType, idName, identity);
		return getInfo(identityInfos, language);
	}

	/**
	 * Gets the identity info.
	 *
	 * @param matchType the match type
	 * @param idName the id name
	 * @param identity the identity
	 * @return the identity info
	 */
	public Map<String, List<IdentityInfoDTO>> getIdentityInfo(MatchType matchType, String idName, RequestDTO identity) {
		Map<String, List<IdentityInfoDTO>> identityInfos = matchType.getIdentityInfoFunction().apply(identity);
		//If this is dynamic match type, filter it based on the id name
		if (matchType.isDynamic()) {
			Map<String, List<IdentityInfoDTO>> filteredIdentityInfos = identityInfos
				.entrySet()
				.stream()
				.filter(e -> e.getKey() != null && e.getValue() != null)
				.filter(e -> e.getKey().equals(idName))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			return filteredIdentityInfos;
		} else {
			return identityInfos;
		}
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
		boolean isEntityLangNull = languageFromEntity == null || languageFromEntity.isEmpty()
				|| languageFromEntity.equalsIgnoreCase("null");
		if (languageFromInput == null) {
			//Since languageFromInput is null entity language also should be null.
			return isEntityLangNull;
		}
		
		if (isEntityLangNull) {
			//Since languageFromInput is not null return false.
			return false;
		}
		
		return languageFromInput.equalsIgnoreCase(languageFromEntity);
	}

	/**
	 * Gets the validate OTP function.
	 *
	 * @return the validate OTP function
	 */
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

	/**
	 * Gets the cbeff values.
	 *
	 * @param idEntity the id entity
	 * @param types the types
	 * @param matchType the match type
	 * @return the cbeff values
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
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
			List<String> identityBioAttributes = getBioAttributeNames(type, matchType, idEntity);
			for (String bioAttribute : identityBioAttributes) {
				Optional<String> identityValue = getIdentityValue(bioAttribute, null, idEntity).findAny();
				if (identityValue.isPresent()) {
					cbeffValuesForTypes.putAll(getCbeffValuesForCbeffDocType(type, matchType, identityValue.get()));
				} else {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(), String.format(
									IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorMessage(), type.getName()));
				}				
			}
		}
		return cbeffValuesForTypes;
	}
	
	/**
	 * 
	 * @param type
	 * @param matchType
	 * @return
	 */
	private List<String> getBioAttributeNames(CbeffDocType type, MatchType matchType,
			Map<String, List<IdentityInfoDTO>> idEntity) {
		if (matchType.toString().equals(BioMatchType.FGRIMG_COMPOSITE.toString()) ||
				matchType.toString().equals(BioMatchType.FGRMIN_COMPOSITE.toString()) || 
				matchType.toString().equals(BioMatchType.FGRIMG_UNKNOWN.toString())) {
			return idEntity.keySet().stream().filter(bio -> bio.startsWith(BiometricType.FINGER.value().toString()))
					.collect(Collectors.toList());
		}
		if (matchType.toString().equals(BioMatchType.IRIS_COMP.toString()) ||
				matchType.toString().equals(BioMatchType.IRIS_UNKNOWN.toString())) {
			return idEntity.keySet().stream().filter(bio -> bio.startsWith(BiometricType.IRIS.value().toString()))
					.collect(Collectors.toList());
		}
		if (matchType.toString().equals(BioMatchType.FACE.toString())) {
			return List.of(BiometricType.FACE.value());
		}
		if(matchType.toString().equals(BioMatchType.MULTI_MODAL.toString())) {
			return idEntity.keySet().stream().filter(bio -> bio.startsWith(BiometricType.FINGER.value().toString()) ||
						bio.startsWith(BiometricType.IRIS.value().toString()) ||
						bio.startsWith(BiometricType.FACE.value().toString()))
					.collect(Collectors.toList());
		}
		return List.of(type.getType().value() + "_" + matchType.getIdMapping().getSubType());
	}

	/**
	 * Gets the cbeff values for cbeff doc type.
	 *
	 * @param type the type
	 * @param matchType the match type
	 * @param biometricCbeff  the identity value
	 * @return the cbeff values for cbeff doc type
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private Map<String, Entry<String, List<IdentityInfoDTO>>> getCbeffValuesForCbeffDocType(CbeffDocType type,
			MatchType matchType, String biometricCbeff) throws IdAuthenticationBusinessException {
		Map<String, String> birBasedOnType;
		try {
			
			List<BIR> birDataFromXMLType = cbeffUtil.getBIRDataFromXMLType(biometricCbeff.getBytes(), type.getName());
			Function<? super BIR, ? extends String> keyFunction = bir -> {
				BDBInfo bdbInfo = bir.getBdbInfo();
				return bdbInfo.getType().get(0).toString() + "_" 
						+ (bdbInfo.getSubtype() == null || bdbInfo.getSubtype().isEmpty()? "" : bdbInfo.getSubtype().get(0))
						+ (bdbInfo.getSubtype().size() > 1 ?  " "  + bdbInfo.getSubtype().get(1) : "") + "_" 
						+ bdbInfo.getFormat().getType();
			};
			if(birDataFromXMLType.size() == 1) {
				//This is the segmented cbeff
				//This is the most possible case
				birBasedOnType = birDataFromXMLType.stream().collect(Collectors.toMap(keyFunction, bir -> biometricCbeff));
			} else if(birDataFromXMLType.isEmpty()) {
				//This is unlikely
				birBasedOnType = Collections.emptyMap();
			} else {
				//If size is more than one, which is unlikely
				birBasedOnType = birDataFromXMLType.stream().collect(Collectors.toMap(keyFunction, bir -> {
					try {
						return new String(cbeffUtil.createXML(List.of(bir)));
					} catch (Exception e) {
						//Mostly this is unlikely
						throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
					}
				}));
			}
			
		} catch (Exception e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorMessage(), type.getName()), e);
		}
		return birBasedOnType.entrySet().stream()
				.collect(Collectors.toMap(Entry<String, String>::getKey, (Entry<String, String> entry) -> {
					IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
					identityInfoDTO.setValue(entry.getValue());
					List<IdentityInfoDTO> idenityList = new ArrayList<>(1);
					idenityList.add(identityInfoDTO);
					return new SimpleEntry<>(getNameForCbeffName(entry.getKey(), matchType), List.of(identityInfoDTO));
				}));
	}

	/**
	 * Fetch the identity value.
	 *
	 * @param name
	 *            the name
	 * @param languageForMatchType
	 *            the language for match type
	 * @param identityInfo
	 *            the identity info
	 * @return the identity value
	 */
	private Stream<String> getIdentityValue(String name, String languageForMatchType,
			Map<String, List<IdentityInfoDTO>> identityInfo) {
		List<IdentityInfoDTO> identityInfoList = identityInfo.get(name);
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream()
					.filter(idinfo -> checkLanguageType(languageForMatchType, idinfo.getLanguage()))
					.map(idInfo -> idInfo.getValue());
		}

		return Stream.empty();
	}

	/**
	 * Get the Cbeff Name mapped on ID Repo based on Ida Mapping.
	 *
	 * @param cbeffName the cbeff name
	 * @param matchType the match type
	 * @return the name for cbeff name
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

	/**
	 * Gets the environment.
	 *
	 * @return the environment
	 */
	@Override
	public Environment getEnvironment() {
		return environment.getEnvironment();
	}

	/**
	 * Gets the title fetcher.
	 *
	 * @return the title fetcher
	 */
	@Override
	public MasterDataFetcher getTitleFetcher() {
		return masterDataManager::fetchTitles;
	}

	/**
	 * Gets the matching threshold.
	 *
	 * @param key the key
	 * @return the matching threshold
	 */
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

	/**
	 * Gets the type for id name.
	 *
	 * @param idName the id name
	 * @param idMappings the id mappings
	 * @return the type for id name
	 */
	public Optional<String> getTypeForIdName(String idName, IdMapping[] idMappings) {
		return Stream.of(idMappings).filter(idmap -> {
			String thisId = idName.replaceAll("\\d", "");
			String thatId = idmap.getIdname().replace(IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER, "");
			return thisId.equalsIgnoreCase(thatId);
		}).map(IdMapping::getType).findFirst();
	}

	/**
	 * Gets the match function.
	 *
	 * @param authType the auth type
	 * @return the match function
	 */
	@Override
	public TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> getMatchFunction(
			AuthType authType) {
		final TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> defaultFunc = (
				arg1, arg2, arg3) -> (double) 0;
		if (authType instanceof BioAuthType) {
			return bioMatcherUtil::match;
		}
		if (authType instanceof KeyBindedTokenAuthType) {
			return keyBindedTokenMatcherUtil::match;
		} else {
			return defaultFunc;
		}
	}

	/**
	 * Gets the mapping config.
	 *
	 * @return the mapping config
	 */
	@Override
	public MappingConfig getMappingConfig() {
		return idMappingConfig;
	}

	/**
	 * Gets the available dynamic attributes names.
	 *
	 * @param request the request
	 * @return the available dynamic attributes names
	 */
	@Override
	public Set<String> getAvailableDynamicAttributesNames(RequestDTO request) {
		if(request.getDemographics() != null && request.getDemographics().getMetadata() != null) {
			return request.getDemographics().getMetadata().keySet();
		}
		return Set.of();
	}

	/**
	 * Get the demo matcher util
	 * 
	 * @return demoMatcherUtil
	 */
	@Override
	public DemoMatcherUtil getDemoMatcherUtil() {
		return demoMatcherUtil;
	}

	/**
	 * Gets the template default language codes
	 */
	@Override
	public List<String> getTemplatesDefaultLanguageCodes() {
		String languages = EnvUtil.getDefaultTemplateLang();
		if (languages != null) {
			return List.of(languages.split(","));
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the system supported languages. 
	 * Combination of mandatory and optional languages.
	 */
	@Override
	public List<String> getSystemSupportedLanguageCodes() {
		String languages = EnvUtil.getMandatoryLanguages() + ","
				+ EnvUtil.getOptionalLanguages();		
		return List.of(languages.split(","));
	}

	/**
	 * Gets the user preferred languages
	 */
	@Override
	public List<String> getUserPreferredLanguages(Map<String, List<IdentityInfoDTO>> idInfo) {
		String userPreferredLangAttribute = EnvUtil.getUserPrefLangAttrName();		
		if (userPreferredLangAttribute != null) {
			List<IdentityInfoDTO> identityInfoList = idInfo.get(userPreferredLangAttribute);
			if (identityInfoList != null) {
				return identityInfoList.stream().map(IdentityInfoDTO::getValue).collect(Collectors.toList());
			}
			return Collections.emptyList();
		}
		return Collections.emptyList();
	}
}
