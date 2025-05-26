package io.mosip.authentication.service.kyc.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CLAIMS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.EMPTY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MATCHED_TRUST_FRAMEWORKS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MAX_AGE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.METADATA;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.NULL_CONST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRUST_FRAMEWORK;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_PROCESS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_ATTRIB_TIME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_CLAIMS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_CLAIMS_ATTRIBS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ADDRESS_FORMATTED;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.OIDCClientData;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.repository.OIDCClientDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTOV2;
import io.mosip.authentication.core.indauth.dto.VerifiedClaimsAttributes;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.service.VerifiedClaimsService;
import io.mosip.authentication.core.spi.indauth.specs.VerifiedClaimsSpec;
import io.mosip.authentication.service.kyc.helper.KycExchangeResponseDataHelper;
import io.mosip.authentication.service.kyc.specs.MaxAgeTimeSpec;
import io.mosip.authentication.service.kyc.specs.TrustFrameworkSpec;
import io.mosip.authentication.service.kyc.specs.VerificationProcessSpec;
import io.mosip.authentication.service.kyc.util.LocaleMappingUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The implementation of Verified Claims service which retrieves the identity
 * information of the individual id and construct the Verified Claims information.
 *
 * @author Mahammed Taheer
 */

@Service
public class VerifiedClaimsServiceImpl implements VerifiedClaimsService {
	
	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(VerifiedClaimsServiceImpl.class);	

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
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private OIDCClientDataRepository oidcClientDataRepo; 

	@Autowired
	private KycExchangeResponseDataHelper kycExchangeResponseDataHelper;

	@Override
	public String buildVerifiedClaimsMetadata(String verifiedClaimsData, String oidcClientId) 
			throws IdAuthenticationBusinessException {
		Optional<OIDCClientData> oidcClientData = oidcClientDataRepo.findByClientId(oidcClientId);
		if(oidcClientData.isEmpty()) {
			return EMPTY;
		}
		List<String> ignoreClaimsList = Arrays.asList(ignoreClaims);
		List<String> oidcClientAllowedVerifiedClaims = Stream.of(oidcClientData.get().getUserClaims())
													.filter(t -> !ignoreClaimsList.contains(t))
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
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
					"buildVerifiedClaimsMetadata", "Verified Claims metadata in DB: " + verifiedClaimsData);
		List<Map<String, Object>> verifiedClaimsList = null;
		try {
			verifiedClaimsList = mapper.readValue(verifiedClaimsData, 
									new TypeReference<List<Map<String, Object>>>() {});
		} catch (JsonProcessingException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
					"buildVerifiedClaimsMetadata", "Error parsing verified claims data: " + e.getMessage(), e);
		}
		Map<String, Object> verifiedClaimsMap = buildVerifiedClaimsMap(verifiedClaimsList, oidcClientAllowedVerifiedClaims);
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
				"buildVerifiedClaimsMetadata", "Verified Claims Map: " + verifiedClaimsMap);

		oidcClientAllowedVerifiedClaims.stream().filter(claim -> !verifiedClaimsMap.keySet().contains(claim))
												.forEach(claim -> verifiedClaimsMap.put(claim, NULL_CONST)); 
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
				"buildVerifiedClaimsMetadata", "Verified Claims: " + verifiedClaimsMap.keySet());
		return convertMapToJsonString(verifiedClaimsMap);							
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> buildVerifiedClaimsMap(List<Map<String, Object>> verifiedClaimsList, 
					List<String> oidcClientAllowedVerifiedClaims) {

		Map<String, Object> verifiedClaimsMap = new HashMap<>();

		if (verifiedClaimsList == null) {
			return verifiedClaimsMap;
		}
		
		Map<String, String> idAttribsMap = getIdAttribsMap(oidcClientAllowedVerifiedClaims);
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
				"buildVerifiedClaimsMap", "ID Attribs Map: " + idAttribsMap);
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
				"buildVerifiedClaimsMap", "OIDC Client Allowed Verified Claims: " + oidcClientAllowedVerifiedClaims);
		try {
			verifiedClaimsList.stream()
				.forEach(verifiedClaim -> {
					List<String> claims = (List<String>) verifiedClaim.get(CLAIMS);
					claims.stream()
						.filter(attributeName -> idAttribsMap.containsKey(attributeName))
						.forEach(attributeName -> {
							String claimName = idAttribsMap.get(attributeName);
							List<Object> metadataLst = (List<Object>) verifiedClaimsMap.computeIfAbsent(claimName, k -> new ArrayList<>());
							Map<String, Object> metadata = (Map<String, Object>) verifiedClaim.get(METADATA);
							metadataLst.add(metadata);
						});
				});
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
					"buildVerifiedClaimsMap", "Error processing verified claims: " + e.getMessage(), e);
		}
		return verifiedClaimsMap;
	}

	private Map<String, String> getIdAttribsMap(List<String> oidcClientAllowedVerifiedClaims) {
		return oidcClientAllowedVerifiedClaims.stream().flatMap(claim -> {
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
	}

	private String convertMapToJsonString(Map<String, Object> verifiedClaimsMap) {
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
				"convertMapToJsonString", "Verified Claims Map: " + verifiedClaimsMap);
		try {
			return mapper.writeValueAsString(verifiedClaimsMap);	
		} catch (JsonProcessingException exp) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "buildVerifiedClaimsMetadata",
					"Error converting map to string. " + exp.getMessage(), exp);
		}
		return EMPTY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String buildExchangeVerifiedClaimsData(String subject, Map<String, List<IdentityInfoDTO>> idInfo,
			List<String> unverifiedConsentClaims, List<String> verifiedConsentClaims, 
			List<String> consentedLocales, String idVid,
			KycExchangeRequestDTOV2 kycExchangeRequestDTOV2) throws IdAuthenticationBusinessException {
			
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
			"buildKycExchangeResponseV2",
			"Building claims response for Id Hash: ");
		
		Set<String> uniqueConsentedLocales = new HashSet<String>(consentedLocales);
		// Mapping the consented locales to their two-letter language codes
		// Eg: eng -> en, fra -> fr, ara -> ar
		Map<String, String> mappedConsentedShortLocales = LocaleMappingUtil.localesMapping(uniqueConsentedLocales);

		Map<String, Object> respMap = new HashMap<>();
		respMap.put(IdAuthCommonConstants.SUBJECT, subject);
		addUnverifiedConsentedClaims(respMap, unverifiedConsentClaims, mappedConsentedShortLocales, idInfo, idVid);
		
		if (respMap.containsValue(null)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		
		List<Map<String, Object>> verifiedClaimsRespLst = new ArrayList<>();
		// verifiedClaimsDBAttributesMap contains the attributes present in the DB
		// Eg: "firstName" -> [VerifiedClaimsAttributes]
		Map<String, List<VerifiedClaimsAttributes>> verifiedClaimsDBAttributesMap = getVerifiedClaimsAttributesMap(idInfo);

		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Verified Claims DB Attributes Map: " + 
												verifiedClaimsDBAttributesMap);


		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Verified Consented Claims Request: " + 
												kycExchangeRequestDTOV2.getVerifiedConsentedClaims());

		int counter = 1;
		for (Map<String, Object> reqVerifiedClaim: Optional.ofNullable(kycExchangeRequestDTOV2.getVerifiedConsentedClaims())
														   .orElse(Collections.emptyList())) {
			
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
										"buildKycExchangeResponseV2",
											"Processing Verified claim object Seq: " + (counter++));
			
			Map<String, Object> reqVerificationMap = (Map<String, Object>) reqVerifiedClaim.get(VERIFICATION);
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
										"buildKycExchangeResponseV2",
											"reqVerificationMap: " + reqVerificationMap);
			if (Objects.nonNull(reqVerificationMap)) {
				// Scenario 1: trust framework object(key) is not available, not adding the requested claims
				if (!reqVerificationMap.containsKey(TRUST_FRAMEWORK) || 
							!reqVerifiedClaim.containsKey(CLAIMS)) {
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
									"Scenario 1: Trust_Framework or Claims Object is not available.");
					continue;
				}

				Map<String, Object> reqClaimsMap = (Map<String, Object>) reqVerifiedClaim.get(CLAIMS);
				mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Processing claim in Scenario 2. reqClaimsMap: " + reqClaimsMap);
				reqClaimsMap.keySet().stream().forEach(claim -> {
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Processing claim in Scenario 2. Claim: " + claim);
					try {
						// "identityDataMap" contains identity data for the claim		
						// Eg: "name" -> "John Smith Jr", "name#en" -> "John Smith Jr", "name#fr" -> "Jean Smithe Jr"
						Map<String, ?> identityDataMap = getIdentityDataForClaim(claim, idInfo, uniqueConsentedLocales);
						List<VerifiedClaimsSpec<?,?>> verificationRequestSpecs = getVerificationRequestSpecs(reqVerificationMap);
						List<VerifiedClaimsAttributes> verifiedClaimsAttributes = getVerifiedClaimsAttributes(claim, verifiedClaimsDBAttributesMap);		
						Map<String, Object> respVerificationMap = new HashMap<>();
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Total number of Verification Specs found. Count: " + verificationRequestSpecs.size());
												
						boolean allSpecsMatched = verificationRequestSpecs.stream().allMatch(spec -> {
							if (spec instanceof TrustFrameworkSpec) {
								TrustFrameworkSpec trustFrameworkSpec = (TrustFrameworkSpec) spec;
								List<String> trustFrameworks = getVerifiedTrustFrameworks(claim, verifiedClaimsDBAttributesMap);
								List<String> matchedTrustFrameworks = trustFrameworkSpec.matchVerifiedClaimsMetadata(trustFrameworks, null);
								if (!matchedTrustFrameworks.isEmpty()) {
									respVerificationMap.put(TRUST_FRAMEWORK, matchedTrustFrameworks.get(0));
								}
								return !matchedTrustFrameworks.isEmpty();
							} else if (spec instanceof MaxAgeTimeSpec) {
								MaxAgeTimeSpec maxAgeTimeSpec = (MaxAgeTimeSpec) spec;
								Map<String, LocalDateTime> verifiedTimeMap = (Map<String, LocalDateTime>) spec.getVerifiedClaimsMetadata(verifiedClaimsAttributes);	
								LocalDateTime matchedTime = maxAgeTimeSpec.matchVerifiedClaimsMetadata(verifiedTimeMap, respVerificationMap);
								if (matchedTime != null) {
									respVerificationMap.put(VERIFIED_ATTRIB_TIME, matchedTime);
								}
								return matchedTime != null;
							} else if (spec instanceof VerificationProcessSpec) {
								VerificationProcessSpec verificationProcessSpec = (VerificationProcessSpec) spec;
								Map<String, VerifiedClaimsAttributes> verificationProcessMap = (Map<String, VerifiedClaimsAttributes>) 
																									spec.getVerifiedClaimsMetadata(verifiedClaimsAttributes);
								String verifiedProcess = verificationProcessSpec.matchVerifiedClaimsMetadata(verificationProcessMap, respVerificationMap);
								if (verifiedProcess != null) {
									respVerificationMap.put(VERIFICATION_PROCESS, verifiedProcess);
								}
								return verifiedProcess != null;
							}
							return false;
						});
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"All Spec Match Result. allSpecsMatched: " + allSpecsMatched);
						// If all the specs are matched, adding the claim to the response
						// In case of multiple claims for same list of trust framework, adding all the claims in the response as list of verification/claims
						if(allSpecsMatched) {
							addVerifiedClaimsToResponse(verifiedClaimsRespLst, respVerificationMap, claim, identityDataMap, mappedConsentedShortLocales);
						}
					} catch (IdAuthenticationBusinessException e) {
						mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
								"buildKycExchangeResponseV2", "Error getting identity data for claim: " + claim, e);
					}
				});
			}
		}
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Verified Claims Response List Size: " + verifiedClaimsRespLst.size());
		if (verifiedClaimsRespLst.size() > 0)
			respMap.put(VERIFIED_CLAIMS, verifiedClaimsRespLst);
		try {
			String signedData = securityManager.signWithPayload(mapper.writeValueAsString(respMap));
			String respType = kycExchangeRequestDTOV2.getRespType();
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"buildKycExchangeResponseV2",
												"Response Type: " + respType);
			if (Objects.nonNull(respType) && respType.equalsIgnoreCase(jweResponseType)){
				String partnerCertData = (String) kycExchangeRequestDTOV2.getMetadata().get(IdAuthCommonConstants.PARTNER_CERTIFICATE);
				return securityManager.jwtEncrypt(signedData, partnerCertData);
			}
			return signedData;
		} catch (JsonProcessingException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	private void addUnverifiedConsentedClaims(Map<String, Object> respMap, List<String> unverifiedConsentClaims, 
				Map<String, String> mappedConsentedLocales, Map<String, List<IdentityInfoDTO>> idInfo, String idVid) {

		unverifiedConsentClaims.stream().filter(attrib -> !attrib.equals(IdAuthCommonConstants.SUBJECT))
				.forEach(attrib -> {
					if (attrib.equals(consentedIndividualAttributeName)) {
						respMap.put(attrib, idVid);		
					} else {
						try {
							List<String> idSchemaAttribute = idInfoHelper.getIdentityAttributesForIdName(attrib);
							// Get all available languages from idInfo for the given attribute
							// Filter mappedConsentedLocales to only include languages available in idInfo
							Map<String, String> filteredLocales = filterMappedConsentedLocales(mappedConsentedLocales, idSchemaAttribute, idInfo);
							mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"addUnverifiedConsentedClaims",
												"Verified Claims idSchemaAttribute: " + idSchemaAttribute);
							mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
											"addUnverifiedConsentedClaims",
											   "Verified Claims attrib: " + attrib);
							if (filteredLocales.size() > 0) {
								kycExchangeResponseDataHelper.addEntityDataForLangCodes(filteredLocales, idInfo, respMap, attrib, idSchemaAttribute);
							}
						} catch (IdAuthenticationBusinessException ex) {
							mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
									"buildKycExchangeResponseV2",
									"Error Processing Unverified claims: " + attrib, ex);
							respMap.put(attrib, null);
						}
					}
				});
	}

	private Map<String, String> filterMappedConsentedLocales(Map<String, String> mappedConsentedLocales, List<String> idSchemaAttribute, 
				Map<String, List<IdentityInfoDTO>> idInfo) {
		// Get all available languages from idInfo for the given attribute
		Set<String> availableLanguages = new HashSet<>();
		idSchemaAttribute.stream()
			.filter(attribute -> idInfo.containsKey(attribute))
			.flatMap(attribute -> idInfo.get(attribute).stream())
			.filter(identityInfo -> identityInfo.getLanguage() != null)
			.map(identityInfo -> identityInfo.getLanguage().toLowerCase())
			.forEach(availableLanguages::add);

		// Filter mappedConsentedLocales to only include languages available in idInfo
		return mappedConsentedLocales.entrySet()
				.stream()
				.filter(entry -> availableLanguages.contains(entry.getKey().toLowerCase()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<VerifiedClaimsAttributes>> getVerifiedClaimsAttributesMap(Map<String, List<IdentityInfoDTO>> idInfo) {
		Map<String, List<VerifiedClaimsAttributes>> verifiedClaimsAttributesMap = new HashMap<>();
		List<IdentityInfoDTO> verifiedClaimsIdentInfoList = idInfo.get(VERIFIED_CLAIMS_ATTRIBS);

		// Verified Claims Attribute are not present in the DB
		if (verifiedClaimsIdentInfoList == null)
			return verifiedClaimsAttributesMap;

		List<Map<String, Object>> verifiedClaimsList = null;
		try {
			verifiedClaimsList = mapper.readValue(verifiedClaimsIdentInfoList.get(0).getValue(), 
									new TypeReference<List<Map<String, Object>>>() {});
		} catch (JsonProcessingException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
					"getVerifiedClaimsAttributesMap", "Error parsing verified claims data: " + e.getMessage(), e);
		}

		if (verifiedClaimsList == null)
			return verifiedClaimsAttributesMap;

		verifiedClaimsList.forEach(verifiedClaim -> {
			List<String> claims  =  (List<String>) verifiedClaim.get(CLAIMS);
			claims.forEach(attributeName -> {
				List<VerifiedClaimsAttributes> verifiedAttribsList = verifiedClaimsAttributesMap.get(attributeName);
				Map<String, Object> metadata = (Map<String, Object>)verifiedClaim.get(METADATA);
				VerifiedClaimsAttributes verifiedAttribs = buildVerifiedAttributes(metadata);
				if (verifiedAttribs != null) {
					if (Objects.isNull(verifiedAttribsList)) {
						verifiedAttribsList = new ArrayList<>();
					}
					verifiedAttribsList.add(verifiedAttribs);
					verifiedClaimsAttributesMap.put(attributeName, verifiedAttribsList);
				} 
			});
		});
		return verifiedClaimsAttributesMap;
	}
	
	private VerifiedClaimsAttributes buildVerifiedAttributes(Map<String, Object> metadata) {
		
		// Trust Framework is present in the metadata, adding the trust framework details to the verified attributes
		// If trust framework is not present, other details are not added
		if (metadata.containsKey(TRUST_FRAMEWORK)) {
			VerifiedClaimsAttributes vAttribs = new VerifiedClaimsAttributes();
			vAttribs.setTrustFramework(String.valueOf(metadata.get(TRUST_FRAMEWORK)));
			if (metadata.containsKey(VERIFIED_ATTRIB_TIME)) {
				vAttribs.setTime(DateUtils.parseToLocalDateTime(String.valueOf(metadata.get(VERIFIED_ATTRIB_TIME))));
			}
			if (metadata.containsKey(VERIFICATION_PROCESS)) {
				vAttribs.setVerificationProcess(String.valueOf(metadata.get(VERIFICATION_PROCESS)));
			}
			return vAttribs;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<VerifiedClaimsSpec<?,?>> getVerificationRequestSpecs(Map<String, Object> reqVerificationMap) {
		
		// Trust Framework is present in the request, adding the trust framework details to the verification request attributes
		// If trust framework is not present, other details are not added
		List<VerifiedClaimsSpec<?,?>> verifiedClaimsSpecs = new ArrayList<>();
		if (reqVerificationMap.containsKey(TRUST_FRAMEWORK)) {
			TrustFrameworkSpec trustFrameworkSpec = new TrustFrameworkSpec(reqVerificationMap.get(TRUST_FRAMEWORK));
			verifiedClaimsSpecs.add(trustFrameworkSpec);
			if (reqVerificationMap.containsKey(VERIFIED_ATTRIB_TIME)) {
				Map<String, Long> maxAgeTimeMap = (Map<String, Long>) reqVerificationMap.get(VERIFIED_ATTRIB_TIME);
				if (maxAgeTimeMap.containsKey(MAX_AGE)) {
					long maxAgeTime = Long.parseLong(String.valueOf(maxAgeTimeMap.get(MAX_AGE)));
					MaxAgeTimeSpec maxAgeTimeSpec = new MaxAgeTimeSpec(maxAgeTime, reqVerificationMap.get(TRUST_FRAMEWORK));
					verifiedClaimsSpecs.add(maxAgeTimeSpec);
				}
			}
			if (reqVerificationMap.containsKey(VERIFICATION_PROCESS)) {
				VerificationProcessSpec verificationProcessSpec = new VerificationProcessSpec(
																		reqVerificationMap.get(VERIFICATION_PROCESS), 
																		reqVerificationMap.get(TRUST_FRAMEWORK));
				verifiedClaimsSpecs.add(verificationProcessSpec);
			}
			return verifiedClaimsSpecs;
		}
		return null;
	}

	private List<String> getVerifiedTrustFrameworks(String claim, 
					Map<String, List<VerifiedClaimsAttributes>> verifiedClaimsDBAttributesMap) {
		try {
			return idInfoHelper.getIdentityAttributesForIdName(claim)
							   .stream()
							   .map(verifiedClaimsDBAttributesMap::get)
							   .filter(Objects::nonNull)
							   .flatMap(List::stream)
							   .map(VerifiedClaimsAttributes::getTrustFramework)
							   .collect(Collectors.toList());
		} catch (IdAuthenticationBusinessException ex) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
							"getVerifiedTrustFrameworks",
							"Error Processing Verified Trustframework Claims: " + claim, ex);
		}
		return new ArrayList<>();
	}

	private List<VerifiedClaimsAttributes> getVerifiedClaimsAttributes(String claim, 
					Map<String, List<VerifiedClaimsAttributes>> verifiedClaimsDBAttributesMap) {
		try {
			return  idInfoHelper.getIdentityAttributesForIdName(claim)
								.stream()
								.map(verifiedClaimsDBAttributesMap::get)
								.filter(Objects::nonNull)
								.flatMap(List::stream)
								.collect(Collectors.toList());
		} catch (IdAuthenticationBusinessException ex) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
							"getVerifiedClaimsAttributes",
							"Error Processing Verified Claims Attributes. Claims: " + claim, ex);
		}
		return new ArrayList<>();	
	}


	private Map<String, ?> getIdentityDataForClaim(String claim, Map<String, List<IdentityInfoDTO>> idInfo, 
					Set<String> uniqueConsentedLocales) throws IdAuthenticationBusinessException {

		List<String> idSchemaAttributes = idInfoHelper.getIdentityAttributesForIdName(claim);

		if (claim.equalsIgnoreCase(consentedAddressAttributeName)) {
			return processLanguageSpecificAttributes(idSchemaAttributes, idInfo, uniqueConsentedLocales, claim);
		}
		
		// First handle attributes with null language
		String attribDataNoLang = idSchemaAttributes.stream()
			.map(idAttr -> idInfo.get(idAttr))
			.filter(Objects::nonNull)
			.map(idInfoList -> idInfoList.stream()
				.filter(info -> info.getLanguage() == null)
				.map(IdentityInfoDTO::getValue)
				.findFirst()
				.orElse(""))
			.filter(value -> !value.isEmpty())
			.collect(Collectors.joining(" "));
		
		if (!attribDataNoLang.isEmpty()) {
			Map<String, String> identityDataMap = new HashMap<>();	
			identityDataMap.put(claim, attribDataNoLang);
			return identityDataMap;
		}

		return processLanguageSpecificAttributes(idSchemaAttributes, idInfo, uniqueConsentedLocales, claim);
	}

	private Map<String, ?> processLanguageSpecificAttributes(List<String> idSchemaAttributes, Map<String, List<IdentityInfoDTO>> idInfo, 
			Set<String> uniqueConsentedLocales, String claim) {

		Map<String, Object> identityDataMap = new HashMap<>();
		// Get available languages from identity data and filter based on consented locales
        Set<String> availableLanguages = idSchemaAttributes.stream()
            .map(idInfo::get)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .map(IdentityInfoDTO::getLanguage)
            .filter(lang -> lang != null && uniqueConsentedLocales.contains(lang))
            .collect(Collectors.toSet());

        // Then process language specific attributes
        availableLanguages.forEach(language -> {
            String attribData = idSchemaAttributes.stream()
                .map(idAttr -> idInfo.get(idAttr))
                .filter(Objects::nonNull)
                .map(idInfoList -> idInfoList.stream()
                    .filter(info -> language.equals(info.getLanguage()) || info.getLanguage() == null)
                    .map(IdentityInfoDTO::getValue)
                    .findFirst()
                    .orElse(""))
                .filter(value -> !value.isEmpty())
                .collect(Collectors.joining(" "));

            if (!attribData.isEmpty()) {
                // Add default name if not already added
                if (!identityDataMap.containsKey(claim)) {
                    identityDataMap.put(claim, claim.equalsIgnoreCase(consentedAddressAttributeName) ? 
									Collections.singletonMap(ADDRESS_FORMATTED, attribData) : attribData);
                }
                
                // Add language specific name
                String shortLangCode = language.substring(0, 2).toLowerCase();
                identityDataMap.put(claim + "#" + shortLangCode, claim.equalsIgnoreCase(consentedAddressAttributeName) ? 
					Collections.singletonMap(ADDRESS_FORMATTED  + "#" + shortLangCode, attribData) : attribData);
            }
        });

        return identityDataMap;
	}

	private void addVerifiedClaimsToResponse(List<Map<String, Object>> verifiedClaimsRespLst, Map<String, Object> respVerificationMap, 
					String claim, Map<String, ?> identityDataMap, Map<String, String> mappedConsentedShortLocales) {

		// just making sure that the matched trust frameworks are not added to the response
		respVerificationMap.remove(MATCHED_TRUST_FRAMEWORKS);
		boolean isAdded = false;
		if(verifiedClaimsRespLst.size() > 0) {
			isAdded = checkAndAddVerifiedClaimsResp(verifiedClaimsRespLst, respVerificationMap, claim, identityDataMap);
		} 
		if (!isAdded) {
			
			Map<String, Object> verifiedClaimsRespMap = new HashMap<>();
			// Add language specific claims if multiple locales exist
			Set<String> availableLocales = identityDataMap.keySet().stream()
				.filter(key -> key.contains("#"))
				.map(key -> key.substring(key.indexOf("#") + 1))
				.collect(Collectors.toSet());
			
			Set<String> requestedLocales = new HashSet<>(mappedConsentedShortLocales.values());
			// Find common locales between available and requested
			Set<String> matchedLocales = availableLocales.stream()
													.filter(requestedLocales::contains)
													.collect(Collectors.toSet());
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
				"buildKycExchangeResponseV2", "Matched locales: " + matchedLocales);

			// If available locales are more than 1, add claims for both en and ar locales
			if (matchedLocales.size() > 1) {
				// Add claims for both en and ar locales
				Map<String, Object> langSpecificClaimMap = new HashMap<>();
				for (String shortLangCode : matchedLocales) {
					String langSpecificClaim = claim + "#" + shortLangCode;
					if (identityDataMap.containsKey(langSpecificClaim)) {
						langSpecificClaimMap.put(langSpecificClaim, identityDataMap.get(langSpecificClaim));
					}
				}
				verifiedClaimsRespMap.put(CLAIMS, langSpecificClaimMap);
			}
			else {
				Map<String, Object> respClaimMap = new HashMap<>();
				respClaimMap.put(claim, identityDataMap.get(claim));
				verifiedClaimsRespMap.put(CLAIMS, respClaimMap);
			} 
			verifiedClaimsRespMap.put(VERIFICATION, respVerificationMap);
			verifiedClaimsRespLst.add(verifiedClaimsRespMap);
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkAndAddVerifiedClaimsResp(List<Map<String, Object>> verifiedClaimsRespLst, Map<String, Object> respVerificationMap, 
					String claim, Map<String, ?> identityDataMap) {
		final List<Boolean> addedClaims = new ArrayList<>();	
		verifiedClaimsRespLst.stream()
			.filter(verifiedClaimsResp -> {
				Map<String, Object> verificationMap = (Map<String, Object>) verifiedClaimsResp.get(VERIFICATION);
				return verificationMap.entrySet().stream()
					.allMatch(entry -> entry.getValue().equals(respVerificationMap.get(entry.getKey())));
			})
			.findFirst()
			.ifPresent(verifiedClaimsResp -> {
				Map<String, Object> existingClaimsMap = (Map<String, Object>) verifiedClaimsResp.get(CLAIMS);
				if (existingClaimsMap != null) {
				existingClaimsMap.put(claim, identityDataMap.get(claim));
				verifiedClaimsResp.put(CLAIMS, existingClaimsMap);
				addedClaims.add(true);
				}
			});
		return !addedClaims.isEmpty();
	}
	
}
