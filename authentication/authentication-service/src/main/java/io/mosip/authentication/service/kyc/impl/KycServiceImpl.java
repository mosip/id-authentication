package io.mosip.authentication.service.kyc.impl;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.LANG_CODE_SEPARATOR;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The implementation of Kyc Authentication service which retrieves the identity
 * information of the individual id and construct the KYC information.
 *
 * @author Sanjay Murali
 */

@Service
public class KycServiceImpl implements KycService {
	
	/** The Constant KYC_ATTRIB_LANGCODE_SEPERATOR. */
	private static final String KYC_ATTRIB_LANGCODE_SEPERATOR = LANG_CODE_SEPARATOR;

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(KycServiceImpl.class);	

	/** The env. */
	@Autowired
	EnvUtil env;

	/** The demo helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/** The mapping config. */
	@Autowired
	private MappingConfig mappingConfig;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/**
	 * Retrieve kyc info.
	 *
	 * @param allowedkycAttributes the allowedkyc attributes
	 * @param langCodes the lang codes
	 * @param identityInfo the identity info
	 * @return the kyc response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.service.KycService#retrieveKycInfo(
	 * java.lang.String, java.util.List, java.lang.String, java.util.Map)
	 */
	@Override
	public KycResponseDTO retrieveKycInfo(List<String> allowedkycAttributes, Set<String> langCodes,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException {
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		if (Objects.nonNull(identityInfo) && Objects.nonNull(allowedkycAttributes) && !allowedkycAttributes.isEmpty()) {
			Optional<String> faceAttribute = allowedkycAttributes.stream()
					.filter(key -> key.equalsIgnoreCase(IdAuthCommonConstants.PHOTO)
							|| key.equalsIgnoreCase(CbeffDocType.FACE.getType().value()))
					.findFirst();
			if(faceAttribute.isPresent()) {
				Map<String, String> faceEntityInfoMap = idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, identityInfo,
						null);
				if (Objects.nonNull(faceEntityInfoMap)) {
					String face = Objects.nonNull(faceEntityInfoMap)
							? faceEntityInfoMap.get(CbeffDocType.FACE.getType().value())
							: null;
					List<IdentityInfoDTO> bioValue = new ArrayList<>();
					IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
					identityInfoDTO.setValue(face);
					bioValue.add(identityInfoDTO);
					identityInfo.put(faceAttribute.get(), bioValue);
				}
			}

			Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = filterIdentityInfo(allowedkycAttributes,
					identityInfo, langCodes);
			if (Objects.nonNull(filteredIdentityInfo)) {
				setKycInfo(allowedkycAttributes, kycResponseDTO, filteredIdentityInfo, langCodes);
			}
		}
		return kycResponseDTO;
	}

	/**
	 * Set KYC info based on the ID Names (from ID Mapping). FACE is also included if required.
	 *
	 * @param allowedkycAttributes the allowedkyc attributes
	 * @param kycResponseDTO the kyc response DTO
	 * @param bioValue the bio value
	 * @param face the face
	 * @param filteredIdentityInfo the filtered identity info
	 * @param langCodes the lang codes
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void setKycInfo(List<String> allowedkycAttributes, KycResponseDTO kycResponseDTO,
			Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, Set<String> langCodes) throws IdAuthenticationBusinessException {
		Map<String, Object> idMappingIdentityInfo = getKycInfo(allowedkycAttributes,
				filteredIdentityInfo, langCodes);
		try {
			kycResponseDTO.setIdentity(mapper.writeValueAsString(idMappingIdentityInfo));
		} catch (JsonProcessingException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Gets the kyc info.
	 *
	 * @param allowedkycAttributes the allowedkyc attributes
	 * @param bioValue the bio value
	 * @param face the face
	 * @param filteredIdentityInfo the filtered identity info
	 * @param langCodes the lang codes
	 * @return the kyc info
	 */
	private Map<String, Object> getKycInfo(List<String> allowedkycAttributes, Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, Set<String> langCodes) {

		Map<String, Object> idMappingIdentityInfo = new HashMap<>();
		for (String kycAttribute : allowedkycAttributes) {
			String mappedIdName = IdaIdMapping.getIdNameForMapping(kycAttribute, mappingConfig).orElse("");
			String idname;
			if (mappedIdName.isEmpty() || mappedIdName.equalsIgnoreCase(DemoMatchType.DYNAMIC.getIdMapping().getIdname())) {
				idname = kycAttribute;
				Map<String, Object> nonMappedIdInfoForIdName = getNonMappedIdentityInfosForIdName(filteredIdentityInfo, langCodes, idname);
				if(!nonMappedIdInfoForIdName.isEmpty()) {
					idMappingIdentityInfo.putAll(nonMappedIdInfoForIdName);
				}
			} else {
				idname = mappedIdName;
				Map<String, Object> idMappingIdentityInfoForIdName = getMappedIdentityInfosForIdName(filteredIdentityInfo, langCodes, idname);
				idMappingIdentityInfo.putAll(idMappingIdentityInfoForIdName);
				//If mapped ID Name is not found, it might be biometrics such as face, for which non mapped id attribute needs to be fetched
				if(idMappingIdentityInfoForIdName.isEmpty()) {
					Map<String, Object> nonMappedIdInfoForIdName = getNonMappedIdentityInfosForIdName(filteredIdentityInfo, langCodes, idname);
					if(!nonMappedIdInfoForIdName.isEmpty()) {
						idMappingIdentityInfo.putAll(nonMappedIdInfoForIdName);
					}
				}
			}
		}
		
		return idMappingIdentityInfo;
	}

	private Map<String, Object> getNonMappedIdentityInfosForIdName(
			Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, Set<String> langCodes, String idName) {
		return getDynamicEntityInfoStream(filteredIdentityInfo, langCodes, idName)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (val1, val2) -> val1));
	}

	private Map<String, Object> getMappedIdentityInfosForIdName(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo,
			Set<String> langCodes, String targetIdName) {
		// Getting allowed demographic data - key will be the ID Name (from ID Mapping),
		// value will be the ID Entity value from ID Name
		Map<String, Object> idMappingIdentityInfo = Stream.of(DemoMatchType.values())
				.flatMap(demoMatchType -> {
					if(demoMatchType.isMultiLanguage()) {
						if(demoMatchType.equals(DemoMatchType.DYNAMIC)) {
							Map<String, List<String>> dynamicAttributes = mappingConfig.getDynamicAttributes();
							return dynamicAttributes.entrySet()
										.stream()
										.map(Entry::getKey)
										.filter(idName -> idName.equalsIgnoreCase(targetIdName))
										.flatMap(idName -> {
											return getDynamicEntityInfoStream(filteredIdentityInfo, langCodes, idName);
										});
						} else {
							String idname = demoMatchType.getIdMapping().getIdname();
							if(targetIdName.equalsIgnoreCase(idname)) {
								return getEntityForLangCodes(filteredIdentityInfo, langCodes, demoMatchType);
							} else {
								return Stream.empty();
							}
						}
					} else {
						String idname = demoMatchType.getIdMapping().getIdname();
						// Need special handling for age since it is mapped to Date of Birth which will
						// conflict with actual date of birth
						if (demoMatchType.equals(DemoMatchType.AGE)) {
							if (targetIdName.equalsIgnoreCase(IdaIdMapping.AGE.getIdname())) {
								return Stream.of(new SimpleEntry<>(IdaIdMapping.AGE.getIdname(),
										getEntityForMatchType(demoMatchType, filteredIdentityInfo)));
							}
						} else {
							if (targetIdName.equalsIgnoreCase(idname)) {
								return Stream.of(new SimpleEntry<>(idname,
										getEntityForMatchType(demoMatchType, filteredIdentityInfo)));
							}
						}
						
						return Stream.empty();
					}
				})
				.filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (val1, val2) -> val1));
		return idMappingIdentityInfo;
	}

	/**
	 * Gets the entity for lang codes and id name.
	 *
	 * @param filteredIdentityInfo the filtered identity info
	 * @param langCodes the lang codes
	 * @param idName the id name
	 * @return the entity for lang codes and id name
	 */
	private Stream<? extends Entry<String, String>> getDynamicEntityInfoStream(
			Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, Set<String> langCodes,
			String idName) {
		return langCodes.stream()
				.flatMap(langCode -> {
					Map<String, String> entityInfo = idInfoHelper.getDynamicEntityInfoAsStringWithKey(filteredIdentityInfo, langCode, idName);
					return entityInfo.entrySet().stream();
				});
	}

	/**
	 * Gets the entity for lang codes.
	 *
	 * @param filteredIdentityInfo the filtered identity info
	 * @param langCodes the lang codes
	 * @param demoMatchType the demo match type
	 * @return the entity for lang codes
	 */
	private Stream<SimpleEntry<String, String>> getEntityForLangCodes(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo,
			Set<String> langCodes, DemoMatchType demoMatchType) {
		return langCodes.stream()
				.map(langCode -> new SimpleEntry<>(
						demoMatchType.getIdMapping().getIdname() + KYC_ATTRIB_LANGCODE_SEPERATOR
								+ langCode,
						getEntityForMatchType(demoMatchType, filteredIdentityInfo, langCode)));
	}
	
	/**
	 * Gets the entity for match type.
	 *
	 * @param matchType the match type
	 * @param filteredIdentityInfo the filtered identity info
	 * @return the entity for match type
	 */
	private String getEntityForMatchType(MatchType matchType, Map<String, List<IdentityInfoDTO>> filteredIdentityInfo) {
		try {
			return idInfoHelper.getEntityInfoAsString(matchType, filteredIdentityInfo);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getEntityForMatchType",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
		}
		return null;
	}
	
	/**
	 * Gets the entity for match type.
	 *
	 * @param matchType the match type
	 * @param filteredIdentityInfo the filtered identity info
	 * @param langCode the lang code
	 * @return the entity for match type
	 */
	private String getEntityForMatchType(MatchType matchType, Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, String langCode) {
		try {
			return idInfoHelper.getEntityInfoAsString(matchType, langCode, filteredIdentityInfo);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getEntityForMatchType",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
		}
		return null;
	}
	
	/**
	 * Construct identity info - Method to filter the details to be printed.
	 *
	 * @param allowedKycType the attributes defined as per policy
	 * @param identity       the identity information of the resident
	 * @param langCodes the lang codes
	 * @return the map returns filtered information defined as per policy
	 */
	private Map<String, List<IdentityInfoDTO>> filterIdentityInfo(List<String> allowedKycType,
			Map<String, List<IdentityInfoDTO>> identity, Set<String> langCodes) {
		Map<String, List<IdentityInfoDTO>> identityInfo = null;
		Map<String, List<IdentityInfoDTO>> identityInfos = null;
		if (Objects.nonNull(allowedKycType)) {
			identityInfo = identity.entrySet().stream().filter(id -> allowedKycType.contains(id.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		if (Objects.nonNull(identityInfo)) {
			identityInfos = identityInfo.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry
					.getValue().stream()
					.filter((IdentityInfoDTO info) -> Objects.isNull(info.getLanguage())
							|| info.getLanguage().equalsIgnoreCase("null")
							|| langCodes.stream().anyMatch(code -> code.equalsIgnoreCase(info.getLanguage())))
					.collect(Collectors.toList())));
		}
		return identityInfos;
	}

}