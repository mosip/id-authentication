package io.mosip.authentication.service.kyc.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.COLON;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.JWK_KEY_TYPE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PUBLIC_KEY_EXPONENT_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PUBLIC_KEY_MODULUS_KEY;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apicatalog.jsonld.document.JsonDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import foundation.identity.jsonld.ConfigurableDocumentLoader;
import foundation.identity.jsonld.JsonLDException;
import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.canonicalizer.URDNA2015Canonicalizer;
import io.mosip.authentication.common.service.entity.CredSubjectIdStore;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.repository.CredSubjectIdStoreRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.VCFormats;
import io.mosip.authentication.core.constant.VCStatus;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.VCResponseDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.service.VciService;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.authentication.service.kyc.util.VCSchemaProviderUtil;
import io.mosip.biometrics.util.ConvertRequestDto;
import io.mosip.biometrics.util.face.FaceDecoder;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.spi.CbeffUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The implementation of Verifiable Credential Issuance service.
 *
 * @author Mahammed Taheer
 */

@Service
public class VciServiceImpl implements VciService {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(VciServiceImpl.class);
	
	private static final ObjectMapper OBJECT_MAPPER;
	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.registerModule(new AfterburnerModule());
	}

	@Value("${ida.idp.consented.individual_id.attribute.name:individual_id}")
	private String consentedIndividualAttributeName;

	@Value("${mosip.ida.config.server.file.storage.uri:}")
	private String configServerFileStorageUrl;
	
	@Value("#{${mosip.ida.vercred.context.url.map}}")
	private Map<String, String> vcContextUrlMap;

	@Value("${mosip.ida.vercred.context.uri:}")
	private String vcContextUri;

	@Value("${mosip.ida.vercred.id.url:}")
	private String verCredIdUrl;

	@Value("${ida.idp.consented.picture.attribute.prefix:data:image/jpeg;base64,}")
	private String consentedPictureAttributePrefix;

	@Value("${mosip.ida.vercred.issuer.url:}")
	private String verCredIssuer;

	@Value("${mosip.ida.vercred.proof.purpose:}")
	private String proofPurpose;

	@Value("${mosip.ida.vercred.proof.type:}")
	private String proofType;

	@Value("${mosip.ida.vercred.proof.verificationmethod:}")
	private String verificationMethod;
	
	private ConfigurableDocumentLoader confDocumentLoader;

	private JSONObject vcContextJsonld;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private CredSubjectIdStoreRepository csidStoreRepo;

	@Autowired
	private VCSchemaProviderUtil vcSchemaProviderUtil;

	/** The demo helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	@Autowired
	private CbeffUtil cbeffUtil;

	@PostConstruct
	private void init() throws IdAuthenticationBusinessException {
		if(Objects.isNull(vcContextUrlMap)){
			mosipLogger.warn(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "VciServiceImpl::init", 
				"Warning - Verifiable Credential Context URL Map not configured, VC generation may fail.");
			confDocumentLoader = new ConfigurableDocumentLoader();
			confDocumentLoader.setEnableHttps(true);
			confDocumentLoader.setEnableHttp(true);
			confDocumentLoader.setEnableFile(false);
		} else {
			Map<URI, JsonDocument> jsonDocumentCacheMap = new HashMap<URI, JsonDocument> ();
			vcContextUrlMap.keySet().stream().forEach(contextUrl -> {
				String localConfigUri = vcContextUrlMap.get(contextUrl);
				JsonDocument jsonDocument = vcSchemaProviderUtil.getVCContextSchema(configServerFileStorageUrl, localConfigUri);
				try {
					jsonDocumentCacheMap.put(new URI(contextUrl), jsonDocument);
				} catch (URISyntaxException e) {
					mosipLogger.warn(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "VciServiceImpl::init", 
							"Warning - Verifiable Credential URI not able to add to cacheMap.");
				} 
			});
			confDocumentLoader = new ConfigurableDocumentLoader(jsonDocumentCacheMap);
			confDocumentLoader.setEnableHttps(false);
			confDocumentLoader.setEnableHttp(false);
			confDocumentLoader.setEnableFile(false);
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "VciServiceImpl::init", 
					"Added cache for the list of configured URL Map: " + jsonDocumentCacheMap.keySet().toString());
		}
		vcContextJsonld = vcSchemaProviderUtil.getVCContextData(configServerFileStorageUrl, vcContextUri, OBJECT_MAPPER);
	}

	@Override
	public void addCredSubjectId(String credSubjectId, String idVidHash, String tokenId, String oidcClientId)
			throws IdAuthenticationBusinessException {

		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addCredSubjectId",
                                    "Add Cred Subject Id for Id/Vid:" + idVidHash);
		String[] didArray = StringUtils.split(credSubjectId, COLON);
		String identityJwk = new String(CryptoUtil.decodeBase64(didArray[2]));
		JSONObject jsonObject = null;
		try {
			jsonObject = OBJECT_MAPPER.readValue(identityJwk, JSONObject.class);
		} catch (IOException ioe) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addCredSubjectId",
				"Error parsing Identity JWK", ioe);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, ioe);
		}
		
		String identityKeyHash = getPublicKeyHash(jsonObject);
		List<CredSubjectIdStore> credSubjectIdList = csidStoreRepo.findAllByCsidKeyHash(identityKeyHash);
		// Case 0: key not exists. List size is zero
		if (credSubjectIdList.size() == 0) {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addCredSubjectId",
                                    "Input Key not present, adding the did jwk in the store.");
			addCredSubjectId(credSubjectId, idVidHash, tokenId, oidcClientId, identityKeyHash);
			return;
		}
		
		// Case 1: key exists but mapped to same id/vid and same token id
		boolean sameIdVid = credSubjectIdList.stream().anyMatch(credSubId -> credSubId.getIdVidHash().equals(idVidHash) && 
					credSubId.getTokenId().equals(tokenId));
		if (sameIdVid) {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addCredSubjectId",
                                    "Input Key already available and mapped to same id/vid and token id.");
			return;
		}
		// Case 2: key exists but mapped to different id/vid and same token id.
		boolean diffIdVidSameToken = credSubjectIdList.stream().anyMatch(credSubId -> !credSubId.getIdVidHash().equals(idVidHash) && 
					credSubId.getTokenId().equals(tokenId));
		if (diffIdVidSameToken) {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addCredSubjectId",
                                    "Input Key already available and mapped to different id/vid but mapped to same token id. " +
									"So, adding new entry in store.");
			addCredSubjectId(credSubjectId, idVidHash, tokenId, oidcClientId, identityKeyHash);
			return;
		}

		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addCredSubjectId",
                                    "Input Key already available and mapped to different id/vid & token id. " +
									"Not allowed to map to input id/vid.");
		throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.KEY_ALREADY_MAPPED_ERROR.getErrorCode(),
                    IdAuthenticationErrorConstants.KEY_ALREADY_MAPPED_ERROR.getErrorMessage());

	}

	private String getPublicKeyHash(JSONObject jsonObject) throws IdAuthenticationBusinessException{
        
        try {
			String publicKeyExponent = jsonObject.get(PUBLIC_KEY_EXPONENT_KEY).toString();
			String publicKeyModulus = jsonObject.get(PUBLIC_KEY_MODULUS_KEY).toString();
			String keyType = jsonObject.get(JWK_KEY_TYPE).toString();
			if (keyType.equalsIgnoreCase(IdAuthCommonConstants.ALGORITHM_RSA)) {
				KeyFactory keyfactory = KeyFactory.getInstance(IdAuthCommonConstants.ALGORITHM_RSA);
				BigInteger modulus = new BigInteger(1, CryptoUtil.decodeBase64Url(publicKeyModulus));
				BigInteger exponent = new BigInteger(1, CryptoUtil.decodeBase64Url(publicKeyExponent));
				PublicKey rsaKey = keyfactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
				return IdAuthSecurityManager.generateHashAndDigestAsPlainText(rsaKey.getEncoded());
			}
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getPublicKeyHash",
                                    "Not Supported Key type.");
            throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.KEY_TYPE_NOT_SUPPORT.getErrorCode(),
                    IdAuthenticationErrorConstants.KEY_TYPE_NOT_SUPPORT.getErrorMessage());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getPublicKeyHash",
                                    "Error Building Public Key Object.", e);
            throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.CREATE_VCI_PUBLIC_KEY_OBJECT_ERROR.getErrorCode(),
                    IdAuthenticationErrorConstants.CREATE_VCI_PUBLIC_KEY_OBJECT_ERROR.getErrorMessage());
        } 
    }

	private void addCredSubjectId(String credSubjectId, String idVidHash, String tokenId, 
				String oidcClientId, String keyHash) {

		String uuid = UUID.randomUUID().toString();
		CredSubjectIdStore credSubjectIdStore = new CredSubjectIdStore();
		credSubjectIdStore.setId(uuid);
		credSubjectIdStore.setIdVidHash(idVidHash);
		credSubjectIdStore.setTokenId(tokenId);
		credSubjectIdStore.setCredSubjectId(credSubjectId);
		credSubjectIdStore.setOidcClientId(oidcClientId);
		credSubjectIdStore.setCsidKeyHash(keyHash);
		credSubjectIdStore.setCsidStatus(VCStatus.ACTIVE.getStatus()); 
		credSubjectIdStore.setCreatedBy(EnvUtil.getAppId());
		credSubjectIdStore.setCrDTimes(DateUtils.getUTCCurrentDateTime());
		csidStoreRepo.saveAndFlush(credSubjectIdStore);
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addCredSubjectId",
					"Credential subject Id details Saved.");
	}

	@Override
	public VCResponseDTO<?> buildVerifiableCredentials(String credSubjectId, String vcFormat,
			Map<String, List<IdentityInfoDTO>> idInfo, List<String> locales, Set<String> allowedAttributes,
			VciExchangeRequestDTO vciExchangeRequestDTO, String psuToken) throws IdAuthenticationBusinessException {
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "buildVerifiableCredentials",
                                    "Building Verifiable Credentials for format: " + vcFormat);
		
		switch (VCFormats.valueOf(vcFormat.toUpperCase())) {
			case LDP_VC: 
				JsonLDObject ldObject = generateLdpVc(credSubjectId, idInfo, locales, allowedAttributes, vciExchangeRequestDTO, psuToken);
				VCResponseDTO<JsonLDObject> vcResponseDTO = new VCResponseDTO<>();
				vcResponseDTO.setVerifiableCredentials(ldObject);
				return vcResponseDTO;
			case JWT_VC_JSON:
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VCI_NOT_SUPPORTED_ERROR);
			case JWT_VC_JSON_LD:
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VCI_NOT_SUPPORTED_ERROR);
			default:
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VCI_NOT_SUPPORTED_ERROR);
		}
	}

	private JsonLDObject generateLdpVc(String credSubjectId, Map<String, List<IdentityInfoDTO>> idInfo, 
				List<String> locales, Set<String> allowedAttributes, VciExchangeRequestDTO vciExchangeRequestDTO, 
				String psuToken) throws IdAuthenticationBusinessException {

		Map<String, Object> credSubjectMap = getCredSubjectMap(credSubjectId, idInfo, locales, allowedAttributes, vciExchangeRequestDTO);
		try {
			Map<String, Object> verCredJsonObject = new HashMap<>();

			// @Context
			Object contextObj = vcContextJsonld.get("context"); 
			verCredJsonObject.put(IdAuthCommonConstants.VC_AT_CONTEXT, contextObj);

			// vc type
			verCredJsonObject.put(IdAuthCommonConstants.VC_TYPE, vciExchangeRequestDTO.getCredentialsDefinition().getType());

			// vc id
			String vcId = UUID.randomUUID().toString();
			verCredJsonObject.put(IdAuthCommonConstants.VC_ID, verCredIdUrl + vcId);

			// vc issuer
			verCredJsonObject.put(IdAuthCommonConstants.VC_ISSUER, verCredIssuer);

			// vc issuance date
			DateTimeFormatter format = DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern());
			LocalDateTime localdatetime = LocalDateTime.parse(DateUtils.getUTCCurrentDateTimeString(EnvUtil.getDateTimePattern()), format);
			verCredJsonObject.put(IdAuthCommonConstants.VC_ISSUANCE_DATE, DateUtils.formatToISOString(localdatetime));

			// vc credentialSubject
			verCredJsonObject.put(IdAuthCommonConstants.CREDENTIALSUBJECT, credSubjectMap);

			// Build the Json LD Object.
			JsonLDObject vcJsonLdObject = JsonLDObject.fromJsonObject(verCredJsonObject);
			vcJsonLdObject.setDocumentLoader(confDocumentLoader);

			// vc proof
			Date created = Date.from(localdatetime.atZone(ZoneId.systemDefault()).toInstant());
			LdProof vcLdProof = LdProof.builder()
										.defaultContexts(false)
										.defaultTypes(false)
										.type(proofType)
										.created(created)
										.proofPurpose(proofPurpose)
										.verificationMethod(new URI(verificationMethod))
										.build();
										
			URDNA2015Canonicalizer canonicalizer =	new URDNA2015Canonicalizer();
			byte[] vcSignBytes = canonicalizer.canonicalize(vcLdProof, vcJsonLdObject);			
			String vcEncodedData = CryptoUtil.encodeBase64Url(vcSignBytes);

			String jws = securityManager.jwsSignWithPayload(vcEncodedData);

			LdProof ldProofWithJWS = LdProof.builder()
				.base(vcLdProof)
				.defaultContexts(false)
				.jws(jws)
				.build();
			
			ldProofWithJWS.addToJsonLDObject(vcJsonLdObject);
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateLdpVc", 
					"Verifiable Credential Generation completed for the provided data.");
			return vcJsonLdObject;
		} catch (IOException | GeneralSecurityException | JsonLDException | URISyntaxException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateLdpVc",
                                    "Error Building Ldp VC.", e);
            throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.LDP_VC_GENERATION_FAILED.getErrorCode(),
                    IdAuthenticationErrorConstants.LDP_VC_GENERATION_FAILED.getErrorMessage());
		}
	}

	private Map<String, Object> getCredSubjectMap(String credSubjectId, Map<String, List<IdentityInfoDTO>> idInfo, 
				List<String> locales, Set<String> allowedAttributes, VciExchangeRequestDTO vciExchangeRequestDTO) 
				throws IdAuthenticationBusinessException {
		Map<String, Object> credSubjectMap = new HashMap<>();
			
		credSubjectMap.put(IdAuthCommonConstants.VC_ID, credSubjectId);

		for (String attrib : allowedAttributes) {
			if (consentedIndividualAttributeName.equals(attrib)) {
				credSubjectMap.put(vciExchangeRequestDTO.getIndividualIdType(), vciExchangeRequestDTO.getIndividualId());
				continue;
			}
			
			if (attrib.equalsIgnoreCase(BiometricType.FACE.value())) {
				Map<String, String> faceEntityInfoMap = idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null);
				if (Objects.nonNull(faceEntityInfoMap)) {
					try {
						String face = convertJP2ToJpeg(getFaceBDB(faceEntityInfoMap.get(CbeffDocType.FACE.getType().value())));
						if (Objects.nonNull(face))
							credSubjectMap.put(attrib, consentedPictureAttributePrefix + face);
					} catch (Exception e) {
						// Not throwing any exception because others claims will be returned without photo.
						mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "",
								"Error Adding photo to the claims. " + e.getMessage(), e);
					}
					
				}
				continue;
			}
			List<String> idSchemaAttributes = idInfoHelper.getIdentityAttributesForIdName(attrib);
			for (String idSchemaAttribute : idSchemaAttributes) {
				List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);
				if (Objects.isNull(idInfoList))
					continue;
				if (idInfoList.size() == 1) {
					IdentityInfoDTO identityInfo = idInfoList.get(0);
					if (Objects.isNull(identityInfo.getLanguage())) {
						String value = identityInfo.getValue();
						if (Objects.nonNull(value) && (value.trim().length() > 0))
							credSubjectMap.put(idSchemaAttribute, value);
					}
					else {
						Map<String, String> valueMap = new HashMap<>();
						String lang = identityInfo.getLanguage();
						if (locales.contains(lang)) {
							String value = identityInfo.getValue();
							if (Objects.nonNull(value) && (value.trim().length() > 0)) {
								valueMap.put(IdAuthCommonConstants.LANGUAGE_STRING, lang);
								valueMap.put(IdAuthCommonConstants.VALUE_STRING, value);
								credSubjectMap.put(idSchemaAttribute, valueMap);
							}
						}
					}
					continue;
				}
				List<Map<String, String>> valueList = new ArrayList<>();
				for (IdentityInfoDTO identityInfo : idInfoList) {
					Map<String, String> valueMap = new HashMap<>();
					String lang = identityInfo.getLanguage();
					if (locales.contains(lang)) {
						String value = identityInfo.getValue();
						if (Objects.nonNull(value) && (value.trim().length() > 0)) {
							valueMap.put(IdAuthCommonConstants.LANGUAGE_STRING, identityInfo.getLanguage());
							valueMap.put(IdAuthCommonConstants.VALUE_STRING, identityInfo.getValue());
							valueList.add(valueMap);
						}
					}
				}
				if (valueList.size() > 0)
					credSubjectMap.put(idSchemaAttribute, valueList);
			}
		}
		return credSubjectMap;
	}

	private String getFaceBDB(String faceCbeff) throws Exception {
		List<BIR> birDataFromXMLType = cbeffUtil.getBIRDataFromXMLType(faceCbeff.getBytes(), CbeffDocType.FACE.getName());
		if(birDataFromXMLType.isEmpty()) {
			//This is unlikely as if empty the exception would have been thrown already
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		return CryptoUtil.encodeBase64(birDataFromXMLType.get(0).getBdb());
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
}
