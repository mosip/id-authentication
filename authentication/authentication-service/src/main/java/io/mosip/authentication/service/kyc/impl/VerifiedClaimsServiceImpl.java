package io.mosip.authentication.service.kyc.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CLAIMS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.EMPTY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.LANG_CODE_SEPARATOR;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.NULL_CONST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRUST_FRAMEWORK;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUES;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_ATTRIB_TIME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_CLAIMS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_CLAIMS_ATTRIBS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.METADATA;

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
import io.mosip.authentication.core.indauth.dto.VerifiedAttributes;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.indauth.service.VerifiedClaimsService;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.biometrics.util.ConvertRequestDto;
import io.mosip.biometrics.util.face.FaceDecoder;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.spi.CbeffUtil;
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
	
	/** The Constant KYC_ATTRIB_LANGCODE_SEPERATOR. */
	private static final String KYC_ATTRIB_LANGCODE_SEPERATOR = LANG_CODE_SEPARATOR;

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

		JSONArray verifiedClaimArray = new JSONArray(verifiedClaimsData);
		Map<String, Object> verifiedClaimsMap = new HashMap<>();
		verifiedClaimArray.forEach(claim -> {
			JSONObject verifiedClaim = (JSONObject) claim;
			JSONArray claimsArr = verifiedClaim.getJSONArray(CLAIMS);
			claimsArr.forEach(attributeName -> {
				if (attributeName instanceof String) {
					String attributeNameStr = (String) attributeName;
					if (idAttribsMap.containsKey(attributeNameStr)) {
						String claimName = idAttribsMap.get(attributeNameStr);
						List<Object> metadataLst = (List<Object>) verifiedClaimsMap.get(claimName);
						//String metadata = ((JSONObject)verifiedClaim.get(METADATA)).toString();
						Map<String, Object> metadata = ((JSONObject)verifiedClaim.get(METADATA)).toMap();	
						if (Objects.isNull(metadataLst)){
							metadataLst = new ArrayList<>();
							metadataLst.add(metadata);
							verifiedClaimsMap.put(claimName, metadataLst);
						} else {
							metadataLst.add(metadata);
							verifiedClaimsMap.put(claimName, metadataLst);
						}
					}
				} 
			});
		});

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
		
		return null;
	}

	private void addTrustFrameworkDetails(List<String> idSchemaAttributes, List<IdentityInfoDTO> idInfo, 
			List<String> respTrustFrameworkLst, String claim) {

		try {
			Map<String, List<Map<String, Object>>> verifiedAttributesMap = 
							mapper.readValue(idInfo.get(0).getValue(), new TypeReference<>() {});
			for(String idAttr : idSchemaAttributes){
				List<Map<String, Object>> values = (List<Map<String, Object>>) verifiedAttributesMap.get(idAttr);
				if (Objects.nonNull(values)) {
					for (Map<String, Object> value : values) {
						if (!value.containsKey(TRUST_FRAMEWORK))
							continue;

						VerifiedAttributes vAttribs = new VerifiedAttributes();
						vAttribs.setTrustFramework(String.valueOf(value.get(TRUST_FRAMEWORK)));

						if (value.containsKey(VERIFIED_ATTRIB_TIME)) {
							//DateUtils.parseToDate(reqTime, EnvUtil.getDateTimePattern());
							vAttribs.setTime(null);
						}
							
						respTrustFrameworkLst.add(String.valueOf(value.get(TRUST_FRAMEWORK)));
					}
					break;
				}
			} 
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
