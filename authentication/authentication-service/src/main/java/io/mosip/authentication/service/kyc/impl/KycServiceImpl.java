package io.mosip.authentication.service.kyc.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CLAIMS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.EMPTY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.LANG_CODE_SEPARATOR;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.NULL_CONST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRUST_FRAMEWORK;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUES;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_CLAIMS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_CLAIMS_ATTRIBS;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.codec.DecoderException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.KycTokenData;
import io.mosip.authentication.common.service.entity.OIDCClientData;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.repository.KycTokenDataRepository;
import io.mosip.authentication.common.service.repository.OIDCClientDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.KycTokenStatusType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.EKycResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTOV2;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.biometrics.util.ConvertRequestDto;
import io.mosip.biometrics.util.face.FaceDecoder;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.spi.CbeffUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

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

	@Value("${ida.idp.consented.picture.attribute.name:picture}")
	private String consentedFaceAttributeName;

	@Value("${ida.idp.consented.address.attribute.name:address}")
	private String consentedAddressAttributeName;

	@Value("${ida.idp.consented.name.attribute.name:name}")
	private String consentedNameAttributeName;

	@Value("${ida.idp.consented.individual_id.attribute.name:individual_id}")
	private String consentedIndividualAttributeName;

	@Value("${ida.idp.consented.picture.attribute.prefix:data:image/jpeg;base64,}")
	private String consentedPictureAttributePrefix;

	@Value("${mosip.ida.idp.consented.address.subset.attributes:}")
	private String[] addressSubsetAttributes;

	@Value("${ida.idp.consented.address.value.separator: }")
	private String addressValueSeparator;
	
	@Value("${ida.kyc.send-face-as-cbeff-xml:false}")
	private boolean sendFaceAsCbeffXml;

	@Value("${ida.idp.jwe.response.type.constant:JWE}")
	private String jweResponseType;

	@Value("${ida.oidc4ida.ignore.standard.claims.list}")
	private String[] ignoreClaims;

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

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private KycTokenDataRepository kycTokenDataRepo;
	
	@Autowired
	private CbeffUtil cbeffUtil;

	@Autowired
	private OIDCClientDataRepository oidcClientDataRepo; 

	/**
	 * Retrieve kyc info.
	 *
	 * @param allowedkycAttributes the allowedkyc attributes
	 * @param langCodes the lang codes
	 * @param identityInfo the identity info
	 * @return the kyc response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Override
	public EKycResponseDTO retrieveKycInfo(List<String> allowedkycAttributes, Set<String> langCodes,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException {
		EKycResponseDTO kycResponseDTO = new EKycResponseDTO();
		if (Objects.nonNull(identityInfo) && Objects.nonNull(allowedkycAttributes) && !allowedkycAttributes.isEmpty()) {
			Optional<String> faceAttribute = IdInfoHelper.getKycAttributeHasPhoto(allowedkycAttributes);
			if(faceAttribute.isPresent()) {
				Map<String, String> faceEntityInfoMap = idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, identityInfo,
						null);
				String faceCbeff = Objects.nonNull(faceEntityInfoMap)
						? faceEntityInfoMap.get(CbeffDocType.FACE.getType().value())
						: null;
				
				String face;
				if(sendFaceAsCbeffXml) {
					face = faceCbeff;
				} else {
					try {
						face = getFaceBDB(faceCbeff);
					} catch (Exception e) {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(),
								String.format(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorMessage(), CbeffDocType.FACE.getName()), e);
					}
				}
					List<IdentityInfoDTO> bioValue = new ArrayList<>();
					IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
					identityInfoDTO.setValue(face);
					bioValue.add(identityInfoDTO);
					identityInfo.put(faceAttribute.get(), bioValue);
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
	private void setKycInfo(List<String> allowedkycAttributes, EKycResponseDTO kycResponseDTO,
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

	// Taking tokenGenerationTime same as auth response time only as response time is generated based on local timezone.
	@Override
	public String generateAndSaveKycToken(String idHash, String authToken, String oidcClientId, String requestTime, 
				String tokenGenerationTime, String reqTransactionId) throws IdAuthenticationBusinessException {
		
		String uuid = UUID.randomUUID().toString();
		LocalDateTime requestLocalDateTime = IdaRequestResponsConsumerUtil.convertStringDateTimeToLDT(requestTime);
		LocalDateTime tokenIssuedDateTime = IdaRequestResponsConsumerUtil.convertStringDateTimeToLDT(tokenGenerationTime);
		
		String kycToken = generateKycToken(uuid, idHash);
		KycTokenData kycTokenData = new KycTokenData();
		kycTokenData.setKycTokenId(uuid);
		kycTokenData.setIdVidHash(idHash);
		kycTokenData.setKycToken(kycToken);
		kycTokenData.setPsuToken(authToken);
		kycTokenData.setOidcClientId(oidcClientId);
		kycTokenData.setRequestTransactionId(reqTransactionId);
		kycTokenData.setTokenIssuedDateTime(tokenIssuedDateTime);
		kycTokenData.setAuthReqDateTime(requestLocalDateTime);
		kycTokenData.setKycTokenStatus(KycTokenStatusType.ACTIVE.getStatus());
		kycTokenData.setCreatedBy(EnvUtil.getAppId());
		kycTokenData.setCrDTimes(DateUtils.getUTCCurrentDateTime());
		kycTokenDataRepo.saveAndFlush(kycTokenData);
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateAndSaveKycToken",
					"KYC Token Generated & Saved.");
		return kycToken;
	}

	private String generateKycToken(String uuid, String idHash) throws IdAuthenticationBusinessException {
		try {
			byte[] uuidBytes = uuid.getBytes();
			byte[] idHashBytes = IdAuthSecurityManager.decodeHex(idHash);
			ByteBuffer bBuffer = ByteBuffer.allocate(uuidBytes.length + idHashBytes.length);
			bBuffer.put(uuidBytes);
			bBuffer.put(idHashBytes);

			byte[] kycTokenInputBytes = bBuffer.array();
			return securityManager.generateKeyedHash(kycTokenInputBytes);
		} catch (DecoderException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateKycToken",
					"Error Generating KYC Token", e);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	@Override
	public boolean isKycTokenExpire(LocalDateTime tokenIssuedDateTime, String kycToken) throws IdAuthenticationBusinessException {
		LocalDateTime currentTime = LocalDateTime.now();
		
		long diffSeconds = ChronoUnit.SECONDS.between(tokenIssuedDateTime, currentTime);
		
		long adjustmentSeconds = EnvUtil.getKycTokenExpireTimeAdjustmentSeconds();
		ValueRange valueRange = ValueRange.of(0, adjustmentSeconds);

		if (tokenIssuedDateTime != null && !valueRange.isValidIntValue(diffSeconds)) {
			return true;
		}
		return false;
	}


	@Override
	public String buildKycExchangeResponse(String subject, Map<String, List<IdentityInfoDTO>> idInfo, 
				List<String> consentedAttributes, List<String> consentedLocales, String idVid, KycExchangeRequestDTO kycExchangeRequestDTO) throws IdAuthenticationBusinessException {
		
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "buildKycExchangeResponse",
					"Building claims response for PSU token: " + subject);
					
		Map<String, Object> respMap = new HashMap<>();
		Set<String> uniqueConsentedLocales = new HashSet<String>(consentedLocales);
		Map<String, String> mappedConsentedLocales = localesMapping(uniqueConsentedLocales);

		respMap.put(IdAuthCommonConstants.SUBJECT, subject);
		
		for (String attrib : consentedAttributes) {
			if (attrib.equals(IdAuthCommonConstants.SUBJECT))
				continue;
			if (attrib.equals(consentedIndividualAttributeName)) {
				respMap.put(attrib, idVid);
				continue;
			}
			List<String> idSchemaAttribute = idInfoHelper.getIdentityAttributesForIdName(attrib);
			if (mappedConsentedLocales.size() > 0) {
				addEntityForLangCodes(mappedConsentedLocales, idInfo, respMap, attrib, idSchemaAttribute);
			}
		}

		try {
			String signedData = securityManager.signWithPayload(mapper.writeValueAsString(respMap));
			String respType = kycExchangeRequestDTO.getRespType();
			if (Objects.nonNull(respType) && respType.equalsIgnoreCase(jweResponseType)){
				String partnerCertData = (String) kycExchangeRequestDTO.getMetadata().get(IdAuthCommonConstants.PARTNER_CERTIFICATE);
				return securityManager.jwtEncrypt(signedData, partnerCertData);
			}
			return signedData;
		} catch (JsonProcessingException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	private void addEntityForLangCodes(Map<String, String> mappedConsentedLocales, Map<String, List<IdentityInfoDTO>> idInfo, 
				Map<String, Object> respMap, String consentedAttribute, List<String> idSchemaAttributes) 
				throws IdAuthenticationBusinessException {
		
		if (consentedAttribute.equals(consentedFaceAttributeName)) {
			if (!idInfo.keySet().contains(BioMatchType.FACE.getIdMapping().getIdname())) {
				mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
					"Face Bio not found in DB. So not adding to response claims.");
				return;
			}
			Map<String, String> faceEntityInfoMap = idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null);
			if (Objects.nonNull(faceEntityInfoMap)) {
				try {
					String face = convertJP2ToJpeg(getFaceBDB(faceEntityInfoMap.get(CbeffDocType.FACE.getType().value())));
					if (Objects.nonNull(face))
						respMap.put(consentedAttribute, consentedPictureAttributePrefix + face);
				} catch (Exception e) {
					// Not throwing any exception because others claims will be returned without photo.
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "",
							"Error Adding photo to the claims. " + e.getMessage(), e);
				}
				
			}
			return;
		}

		if (idSchemaAttributes.size() == 1) {
			List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttributes.get(0));
			if (Objects.isNull(idInfoList)) {
				mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
					"Data not available in Identity Info for the claim. So not adding to response claims. Claim Name: " + idSchemaAttributes.get(0));
				return;
			}
			Map<String, String> mappedLangCodes = langCodeMapping(idInfoList);
			List<String> availableLangCodes = getAvailableLangCodes(mappedConsentedLocales, mappedLangCodes);
			if (availableLangCodes.size() == 1){
				for (IdentityInfoDTO identityInfo : idInfoList) {
					String langCode = mappedLangCodes.get(availableLangCodes.get(0));
					if (identityInfo.getLanguage().equalsIgnoreCase(langCode)) {
						respMap.put(consentedAttribute, identityInfo.getValue());
					}
				}
			} else {
				if (availableLangCodes.size() > 0) {
					for (IdentityInfoDTO identityInfo : idInfoList) {
						for (String availableLangCode : availableLangCodes) {
							String langCode = mappedLangCodes.get(availableLangCode);
							if (identityInfo.getLanguage().equalsIgnoreCase(langCode)) {
								respMap.put(consentedAttribute + IdAuthCommonConstants.CLAIMS_LANG_SEPERATOR + availableLangCode, 
										identityInfo.getValue());
							}
						}
					}
				} else {
					respMap.put(consentedAttribute, idInfoList.get(0).getValue());
				}
			}
		} else {
			if (consentedAttribute.equals(consentedNameAttributeName)) {
				addNameClaim(mappedConsentedLocales, idInfo, respMap, consentedAttribute, idSchemaAttributes);
			}
			if (consentedAttribute.equals(consentedAddressAttributeName)) {
				if (mappedConsentedLocales.size() > 1) {
					for (String consentedLocale: mappedConsentedLocales.keySet()) {
						String consentedLocaleValue = mappedConsentedLocales.get(consentedLocale);
						if (addressSubsetAttributes.length == 0) {
							mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
									"No address subset attributes configured. Will return the address with formatted attribute.");
							addFormattedAddress(idSchemaAttributes, idInfo, consentedLocaleValue, respMap, true, 
								IdAuthCommonConstants.CLAIMS_LANG_SEPERATOR + consentedLocaleValue);
							continue;
						}
						addAddressClaim(addressSubsetAttributes, idInfo, consentedLocaleValue, respMap, true, 
								IdAuthCommonConstants.CLAIMS_LANG_SEPERATOR + consentedLocaleValue);
					}
				} else {
					String consentedLocale = mappedConsentedLocales.keySet().iterator().next();
					String consentedLocaleValue = mappedConsentedLocales.get(consentedLocale);
					if (addressSubsetAttributes.length == 0) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
								"No address subset attributes configured. Will return the address with formatted attribute.");
						addFormattedAddress(idSchemaAttributes, idInfo, consentedLocaleValue, respMap, false, "");
						return;
					}
					
					addAddressClaim(addressSubsetAttributes, idInfo, consentedLocaleValue, respMap, false, "");
				}
			}
		}
	}

	private void addFormattedAddress(List<String> idSchemaAttributes, Map<String, List<IdentityInfoDTO>> idInfo, String localeValue, 
								Map<String, Object> respMap, boolean addLocale, String localeAppendValue) throws IdAuthenticationBusinessException {
		boolean langCodeFound = false;
		Map<String, String> addressMap = new HashMap<>();
		StringBuilder identityInfoValue = new StringBuilder(); 
		for (String schemaAttrib: idSchemaAttributes) {
			List<String> idSchemaSubsetAttributes = idInfoHelper.getIdentityAttributesForIdName(schemaAttrib);
			for (String idSchemaAttribute : idSchemaSubsetAttributes) {
				List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);
				Map<String, String> mappedLangCodes = langCodeMapping(idInfoList);
				if (identityInfoValue.length() > 0) {
					identityInfoValue.append(addressValueSeparator);
				}
				if (mappedLangCodes.keySet().contains(localeValue)) {
					String langCode = mappedLangCodes.get(localeValue);
					for (IdentityInfoDTO identityInfo : idInfoList) { 
						if (identityInfoValue.length() > 0) {
							identityInfoValue.append(addressValueSeparator);
						}
						if (identityInfo.getLanguage().equals(langCode)) {
							langCodeFound = true;
							identityInfoValue.append(identityInfo.getValue());
						}
					}
				} else {
					if (Objects.nonNull(idInfoList) && idInfoList.size() == 1 && langCodeFound) {
						identityInfoValue.append(idInfoList.get(0).getValue());
					}
				}
			}
		}
		if (identityInfoValue.toString().trim().length() == 0) 
			return;
		//String identityInfoValueStr = identityInfoValue.toString();
		//String trimmedValue = identityInfoValueStr.substring(0, identityInfoValueStr.lastIndexOf(addressValueSeparator));
		addressMap.put(IdAuthCommonConstants.ADDRESS_FORMATTED + localeAppendValue, identityInfoValue.toString());
		if (langCodeFound && addLocale)
			respMap.put(consentedAddressAttributeName + localeAppendValue, addressMap);
		else
			respMap.put(consentedAddressAttributeName, addressMap);
	}

	private void addAddressClaim(String[] addressAttributes, Map<String, List<IdentityInfoDTO>> idInfo, String consentedLocaleValue,
			Map<String, Object> respMap, boolean addLocale, String localeAppendValue) throws IdAuthenticationBusinessException {
		boolean langCodeFound = false; //added for language data not available in identity info (Eg: fr)
		Map<String, String> addressMap = new HashMap<>();
		for (String addressAttribute : addressAttributes) {
			List<String> idSchemaSubsetAttributes = idInfoHelper.getIdentityAttributesForIdName(addressAttribute);
			StringBuilder identityInfoValue = new StringBuilder(); 
			for (String idSchemaAttribute : idSchemaSubsetAttributes) {
				List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);
				Map<String, String> mappedLangCodes = langCodeMapping(idInfoList);
				if (identityInfoValue.length() > 0) {
					identityInfoValue.append(addressValueSeparator);
				}
				if (mappedLangCodes.keySet().contains(consentedLocaleValue)) {
					String langCode = mappedLangCodes.get(consentedLocaleValue);
					for (IdentityInfoDTO identityInfo : idInfoList) {
						if (identityInfoValue.length() > 0) {
							identityInfoValue.append(addressValueSeparator);
						}
						if (identityInfo.getLanguage().equals(langCode)) {
							langCodeFound = true;
							identityInfoValue.append(identityInfo.getValue());
						}
					}
				} else {
					if (Objects.nonNull(idInfoList) && idInfoList.size() == 1 && langCodeFound) {
						identityInfoValue.append(idInfoList.get(0).getValue());
					}
				}
			}
			// Added below condition to skip if the data is not available in DB. MOSIP-26472
			if (identityInfoValue.toString().trim().length() > 0)
				addressMap.put(addressAttribute + localeAppendValue, identityInfoValue.toString());
		}
		if (addressMap.size() == 0) 
			return;

		if (langCodeFound && addLocale)
			respMap.put(consentedAddressAttributeName + localeAppendValue, addressMap);
		else 
			respMap.put(consentedAddressAttributeName, addressMap);
	}

	private void addNameClaim(Map<String, String> mappedConsentedLocales, Map<String, List<IdentityInfoDTO>> idInfo, 
				Map<String, Object> respMap, String consentedAttribute, List<String> idSchemaAttributes) throws IdAuthenticationBusinessException{
		if(mappedConsentedLocales.size() > 1) {
			for (String consentedLocale: mappedConsentedLocales.keySet()) {
				String consentedLocaleValue = mappedConsentedLocales.get(consentedLocale);
				StringBuilder nameBuffer = new StringBuilder();
				for (String idSchemaAttribute : idSchemaAttributes) {
					List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);

					if (Objects.isNull(idInfoList)) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
							"Data not available in Identity Info for the claim. So not adding to response claims. Claim Name: " + idSchemaAttribute);
						continue;
					}
					if (nameBuffer.length() > 0) {
						nameBuffer.append(" ");
					}
					Map<String, String> mappedLangCodes = langCodeMapping(idInfoList);
					if (!mappedLangCodes.keySet().contains(consentedLocaleValue)) {
						break;
					}
					for (IdentityInfoDTO identityInfo : idInfoList) {
						String langCode = mappedLangCodes.get(consentedLocaleValue);
						if (identityInfo.getLanguage().equalsIgnoreCase(langCode)) {
							nameBuffer.append(identityInfo.getValue());
						}
					}
				}
				if (nameBuffer.toString().trim().length() > 0)
					respMap.put(consentedAttribute + IdAuthCommonConstants.CLAIMS_LANG_SEPERATOR + consentedLocaleValue, nameBuffer.toString());
			}
		} else {
			StringBuilder nameBuffer = new StringBuilder();
			for (String idSchemaAttribute : idSchemaAttributes) {
				List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);

				if (Objects.isNull(idInfoList)) {
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
						"Data not available in Identity Info for the claim. So not adding to response claims. Claim Name: " + idSchemaAttribute);
					continue;
				}
				if (nameBuffer.length() > 0) {
					nameBuffer.append(" ");
				}
				Map<String, String> mappedLangCodes = langCodeMapping(idInfoList);
				List<String> availableLangCodes = getAvailableLangCodes(mappedConsentedLocales, mappedLangCodes);
				if (availableLangCodes.size() == 0) {
					continue;
				} 
				for (IdentityInfoDTO identityInfo : idInfoList) {
					String langCode = mappedLangCodes.get(availableLangCodes.get(0));
					if (identityInfo.getLanguage().equalsIgnoreCase(langCode)) {
						nameBuffer.append(identityInfo.getValue());
					}
				}
			}
			if (nameBuffer.toString().trim().length() > 0)
				respMap.put(consentedAttribute, nameBuffer.toString());
		}
	}

	private String convertJP2ToJpeg(String jp2Image) {
		try {
			ConvertRequestDto convertRequestDto = new ConvertRequestDto();
			convertRequestDto.setVersion(IdAuthCommonConstants.FACE_ISO_NUMBER);
			convertRequestDto.setInputBytes(CryptoUtil.decodeBase64(jp2Image));
			byte[] image = FaceDecoder.convertFaceISOToImageBytes(convertRequestDto);
			return CryptoUtil.encodeBase64(image);
		} catch(Exception exp) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "convertJP2ToJpeg",
					"Error Converting JP2 To JPEG. " + exp.getMessage(), exp);
		}
		return null;
	}

	private Map<String, String> localesMapping(Set<String> locales) {

		Map<String, String> mappedLocales = new HashMap<>();
		for (String locale : locales) {
			if (locale.trim().length() == 0)
				continue;
			mappedLocales.put(locale, locale.substring(0, 2));
		}
		return mappedLocales;
	}

	private Map<String, String> langCodeMapping(List<IdentityInfoDTO> idInfoList) {

		Map<String, String> mappedLangCodes = new HashMap<>();
		if (Objects.nonNull(idInfoList)) {
			for (IdentityInfoDTO idInfo :  idInfoList) {
				if (Objects.nonNull(idInfo.getLanguage())) {
					mappedLangCodes.put(idInfo.getLanguage().substring(0,2), idInfo.getLanguage());
				}
			}
		}
		return mappedLangCodes;
	}

	private List<String> getAvailableLangCodes(Map<String, String> mappedLocales, Map<String, String> mappedLangCodes) {
		List<String> availableLangCodes = new ArrayList<>();
		for (String entry: mappedLocales.keySet()) {
			String locale = mappedLocales.get(entry);
			if (mappedLangCodes.keySet().contains(locale)) {
				availableLangCodes.add(locale);
			}
		}
		return availableLangCodes;
	}
	
	private String getFaceBDB(String faceCbeff) throws Exception {
		List<BIR> birDataFromXMLType = cbeffUtil.getBIRDataFromXMLType(faceCbeff.getBytes(), CbeffDocType.FACE.getName());
		if(birDataFromXMLType.isEmpty()) {
			//This is unlikely as if empty the exception would have been thrown already
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		return CryptoUtil.encodeBase64(birDataFromXMLType.get(0).getBdb());
	}

	public String buildVerifiedClaimsMetadata(String verifiedClaimsData, String oidcClientId) 
			throws IdAuthenticationBusinessException {
		Optional<OIDCClientData> oidcClientData = oidcClientDataRepo.findByClientId(oidcClientId);
		if(oidcClientData.isEmpty()) {
			return EMPTY;
		}
		List<String> oidcClientAllowedVerifiedClaims = Stream.of(oidcClientData.get().getUserClaims())
													.filter(t -> !Arrays.asList(ignoreClaims).contains(t))
													.collect(Collectors.toList());
		
		if (verifiedClaimsData.equals(EMPTY)) {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
					"buildVerifiedClaimsMetadata", "No Verified Claims data found for the id.");
			Map<String, Object> verifiedClaimsMap = new HashMap<>(); 
			oidcClientAllowedVerifiedClaims.stream()
			  							   .forEach(claim -> verifiedClaimsMap.put(claim, NULL_CONST));
			return convertMapToJsonString(verifiedClaimsMap);
		}
		
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
					"buildVerifiedClaimsMetadata", "Verified Claims data found for the id.");
		JSONObject verifiedClaimJson = new JSONObject(verifiedClaimsData);
		Set<String> verifiedClaimKeys = StreamSupport.stream(Spliterators.spliteratorUnknownSize(
																verifiedClaimJson.keys(), 0), 
																false).collect(Collectors.toSet());
																
		Map<String, String> idAttribsMap = oidcClientAllowedVerifiedClaims.stream().flatMap(claim -> {
						try {
							return idInfoHelper.getIdentityAttributesForIdName(claim)
							                   .stream()
											   .map(attrib -> new AbstractMap.SimpleEntry<>(attrib, claim));
						} catch (IdAuthenticationBusinessException exp) {
							mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
								"buildVerifiedClaimsMetadata", "Error Fatching the attibutes. " + exp.getMessage(), exp);
						}
						return Stream.empty();
					}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		
		Map<String, Object> verifiedClaimsMap = idAttribsMap.entrySet().stream().filter(e -> verifiedClaimKeys.contains(e.getKey()))
							 		.map(entry -> new Object[] {(Object)entry.getValue(), 
												((JSONArray)verifiedClaimJson.get(entry.getKey())).toList()})
							 		.collect(Collectors.toMap(strArr -> strArr[0].toString(), strArr -> strArr[1]));

		oidcClientAllowedVerifiedClaims.stream().filter(claim -> !verifiedClaimsMap.keySet().contains(claim))
												.forEach(claim -> verifiedClaimsMap.put(claim, NULL_CONST));
		
		return convertMapToJsonString(verifiedClaimsMap);
		
	}

	private String convertMapToJsonString(Map<String, Object> verifiedClaimsMap) {
		String verifiedClaimsStr = null;
		try {
			verifiedClaimsStr = mapper.writeValueAsString(verifiedClaimsMap);	
		} catch (JsonProcessingException exp) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "buildVerifiedClaimsMetadata",
					"Error converting map to string. " + exp.getMessage(), exp);
		}
		return verifiedClaimsStr;
	}

	@Override
	public String buildExchangeVerifiedClaimsData(String subject, Map<String, List<IdentityInfoDTO>> idInfo,
			List<String> unverifiedConsentClaims, List<String> verifiedConsentClaims, 
			List<String> consentedLocales, String idVid,
			KycExchangeRequestDTOV2 kycExchangeRequestDTOV2) throws IdAuthenticationBusinessException {
			
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
			"buildKycExchangeResponseV2",
			"Building claims response for PSU token: " + subject);
		
		Set<String> uniqueConsentedLocales = new HashSet<String>(consentedLocales);
		Map<String, String> mappedConsentedLocales = localesMapping(uniqueConsentedLocales);

		Map<String, Object> respMap = new HashMap<>();
		respMap.put(IdAuthCommonConstants.SUBJECT, subject);
				
		unverifiedConsentClaims.stream().filter(attrib -> !attrib.equals(IdAuthCommonConstants.SUBJECT))
		.forEach(attrib -> {
			if (attrib.equals(consentedIndividualAttributeName)) {
				respMap.put(attrib, idVid);
			} else {
				try {
					List<String> idSchemaAttribute = idInfoHelper.getIdentityAttributesForIdName(attrib);
					if (mappedConsentedLocales.size() > 0) {
						addEntityForLangCodes(mappedConsentedLocales, idInfo, respMap, attrib, idSchemaAttribute);
					}
				} catch (IdAuthenticationBusinessException ex) {
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
							"buildKycExchangeResponseV2",
							"Error Processing Unverified claims: " + attrib, ex);
					respMap.put(attrib, null);
				}
			}
		});
		
		if (respMap.containsValue(null)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		
		List<Map<String, Object>> verifiedClaimsRespLst = new ArrayList<>();
		int counter = 1;
		for (Map<String, Object> reqVerifiedClaim: kycExchangeRequestDTOV2.getVerifiedClaims()) {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
										"buildKycExchangeResponseV2",
											"Processing Verified claim object Seq: " + (counter++));
			Map<String, Object> verifiedClaimsRespMap = null;
			if (Objects.nonNull(reqVerifiedClaim.get(VERIFICATION)) && 
							reqVerifiedClaim.get(VERIFICATION) instanceof Map) {
				Map<String, Object> reqVerificationMap = (Map<String, Object>) reqVerifiedClaim.get(VERIFICATION);
				// Scenario 1: trust framework object(key) is not available, not adding the requested claims
				if (!reqVerificationMap.containsKey(TRUST_FRAMEWORK) || 
							!reqVerifiedClaim.containsKey(CLAIMS)) {
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
									"Scenario 1: Trust_Framework or Claims Object is not available.");
					continue;
				}
				Object reqTrustFramework = reqVerificationMap.get(TRUST_FRAMEWORK);
				Object reqClaims = reqVerifiedClaim.get(CLAIMS);
				// Scenario 2: trust framework object(key) is available and value is null. 
				// adding the requested claims with any trust framework.
				if (Objects.isNull(reqTrustFramework) || ((reqTrustFramework instanceof String) && 
							((String)reqTrustFramework).equalsIgnoreCase(NULL_CONST))) {
					Map<String, Object> reqClaimsMap = (Map<String, Object>) reqClaims;
					Map<String, Object> respClaimsMap = new HashMap<>();
					List<String> respTrustFrameworkLst = new ArrayList<>();
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
									"Scenario 2: Trust_Framework is Null.");
					reqClaimsMap.keySet().stream().forEach(claim -> {
						try {
							mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Processing claim in Scenario 2. Claim: " + claim);
							List<String> idSchemaAttributes = idInfoHelper.getIdentityAttributesForIdName(claim);
							if (mappedConsentedLocales.size() > 0) {
								addEntityForLangCodes(mappedConsentedLocales, idInfo, respClaimsMap, claim, idSchemaAttributes);
							}
							addTrustFrameworkDetails(idSchemaAttributes, idInfo.get(VERIFIED_CLAIMS_ATTRIBS), respTrustFrameworkLst, claim);
						} catch (IdAuthenticationBusinessException ex) {
							mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
									"buildKycExchangeResponseV2",
									"Error Processing Unverified claims: " + claim, ex);
							respClaimsMap.put(claim, null);
						}
					});
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Verified claims found in Scenario 2. Count: "+ respClaimsMap.size());
					if (!respClaimsMap.isEmpty()) {
						Map<String, String> trustFrameworkMap = new HashMap<>();
						if (respTrustFrameworkLst.size() > 0) {
							trustFrameworkMap.put(TRUST_FRAMEWORK, respTrustFrameworkLst.get(0));
						}
						verifiedClaimsRespMap = new HashMap<>();
						verifiedClaimsRespMap.put(CLAIMS, respClaimsMap);
						verifiedClaimsRespMap.put(VERIFICATION, trustFrameworkMap);
					}
				}
				// Scenario 3: trust framework object(key) is available and value is not null. 
				// adding the requested claims with any trust framework.
				if (Objects.nonNull(reqTrustFramework) && (reqTrustFramework instanceof Map)) {
					Map<String, Object> reqClaimsMap = (Map<String, Object>) reqClaims;
					Map<String, Object> reqTrustFrameworkMap = (Map<String, Object>) reqTrustFramework;
					List<String> reqValues = getRequestTrustFrameworks(reqTrustFrameworkMap);
					Map<String, Object> respClaimsMap = new HashMap<>();
					List<String> respTrustFrameworkLst = new ArrayList<>();
					Map<String, String> trustFrameworkMap = new HashMap<>();
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
									"Scenario 3: Trust_Framework is Not Null.");
					reqClaimsMap.keySet().stream().forEach(claim -> {
						try {
							mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Processing claim in Scenario 3. Claim: " + claim);
							List<String> idSchemaAttributes = idInfoHelper.getIdentityAttributesForIdName(claim);
							addTrustFrameworkDetails(idSchemaAttributes, idInfo.get(VERIFIED_CLAIMS_ATTRIBS), respTrustFrameworkLst, claim);
							for (String value : reqValues) {
								if (respTrustFrameworkLst.contains(value)) {
									addEntityForLangCodes(mappedConsentedLocales, idInfo, respClaimsMap, claim, idSchemaAttributes);
									trustFrameworkMap.put(TRUST_FRAMEWORK, value);
									break;
								}
							}
						} catch (IdAuthenticationBusinessException ex) {
							mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
									"buildKycExchangeResponseV2",
									"Error Processing Unverified claims: " + claim, ex);
							respClaimsMap.put(claim, null);
						}
					});
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Verified claims found in Scenario 3. Count: "+ respClaimsMap.size());
					if (!respClaimsMap.isEmpty()) {
						verifiedClaimsRespMap = new HashMap<>();
						verifiedClaimsRespMap.put(CLAIMS, respClaimsMap);
						verifiedClaimsRespMap.put(VERIFICATION, trustFrameworkMap);
					}
				}
			}
			if (Objects.nonNull(verifiedClaimsRespMap))
				verifiedClaimsRespLst.add(verifiedClaimsRespMap);
		}
		if (verifiedClaimsRespLst.size() > 0)
			respMap.put(VERIFIED_CLAIMS, verifiedClaimsRespLst);
		try {
			String signedData = securityManager.signWithPayload(mapper.writeValueAsString(respMap));
			// TODO : the response needs to be encrypted?,  
			/* String respType = kycExchangeRequestDTOV2.getRespType();
			if (Objects.nonNull(respType) && respType.equalsIgnoreCase(jweResponseType)){
				String partnerCertData = (String) kycExchangeRequestDTO.getMetadata().get(IdAuthCommonConstants.PARTNER_CERTIFICATE);
				return securityManager.jwtEncrypt(signedData, partnerCertData);
			} */
			return signedData;
		} catch (JsonProcessingException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	private void addTrustFrameworkDetails(List<String> idSchemaAttributes, List<IdentityInfoDTO> idInfo, 
			List<String> respTrustFrameworkLst, String claim) {

		try {
			Map<String, List<Map<String, Object>>> verifiedAttributesMap = 
							mapper.readValue(idInfo.get(0).getValue(), new TypeReference<>() {});

			/* for(String idAttr : idSchemaAttributes){
				List<Map<String, Object>> values = (List<Map<String, Object>>) verifiedAttributesMap.get(idAttr);
				if (Objects.nonNull(values)) {
					for (Map<String, Object> value : values) {
						respTrustFrameworkLst.add(String.valueOf(value.get(TRUST_FRAMEWORK)));
					}
					break;
				}
			} */
			idSchemaAttributes.stream()
							  .map(verifiedAttributesMap::get) 
							  .filter(Objects::nonNull)        
							  .flatMap(List::stream)           
							  .map(value -> String.valueOf(value.get(TRUST_FRAMEWORK)))
							  .forEach(respTrustFrameworkLst::add);

			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
					"addTrustFrameworkDetails",
						"Added Claim in trust framework list: " + respTrustFrameworkLst);
		} catch (JsonProcessingException ex) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
						"addTrustFrameworkDetails",
						"Error adding claims to trust framework list: " + claim, ex);
		} 
	}

	private List<String> getRequestTrustFrameworks(Map<String, Object> reqTrustFrameworkMap) {
		List<String> values = new ArrayList<String>();
		if (reqTrustFrameworkMap.containsKey(VERIFICATION_VALUE)) {
			String value = (String) reqTrustFrameworkMap.get(VERIFICATION_VALUE);
			values.add(value);
			return values;
		}
		List<String> valueLst = (List<String>) reqTrustFrameworkMap.get(VERIFICATION_VALUES);
		values.addAll(valueLst);
		return values;
	}
}
