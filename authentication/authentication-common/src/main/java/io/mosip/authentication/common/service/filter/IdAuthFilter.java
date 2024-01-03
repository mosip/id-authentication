package io.mosip.authentication.common.service.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import io.mosip.authentication.core.indauth.dto.KeyBindedTokenDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.AuthContextClazzRefProvider;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.DomainType;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.KYCAttributes;
import io.mosip.authentication.core.partner.dto.MispPolicyDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.spi.authtype.acramr.AuthMethodsRefValues;
import io.mosip.authentication.core.spi.authtype.acramr.AuthenticationFactor;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.BytesUtil;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.*;

/**
 * The Class IdAuthFilter - the implementation for deciphering and validation of
 * the authenticating partner done for request as AUTH and KYC.
 *
 * @author Manoj SP
 * @author Sanjay Murali
 * @author Loganathan Sekar
 * @author Nagarjuna K
 */
@Component
public abstract class IdAuthFilter extends BaseAuthFilter {
	
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthFilter.class);

	/** The Constant THUMBPRINT. */
	private static final String THUMBPRINT = "thumbprint";
	
	/** The Constant TRANSACTION_ID. */
	private static final String TRANSACTION_ID = "transactionId";

	private static final String PERIOD = "\\.";

	private static final String JWT_HEADER_CERT_KEY = "x5c";
	
	/** The partner service. */
	protected PartnerService partnerService;
	
	/** The id mapping config. */
	private IDAMappingConfig idMappingConfig;

	/** The Authentication Methods Reference Values */
	private AuthContextClazzRefProvider authContextClazzRefProvider; 

	private AuthMethodsRefValues authMethodsRefValues;
	
	/**
	 * Initialize the filter.
	 *
	 * @param filterConfig the filter config
	 * @throws ServletException the servlet exception
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(filterConfig.getServletContext());

		idMappingConfig = context.getBean(IDAMappingConfig.class);
		// Internal auth is not depending on partner service
		try {
			partnerService = context.getBean(PartnerService.class);
		} catch (NoSuchBeanDefinitionException ex) {
			//
		}
		authContextClazzRefProvider = context.getBean(AuthContextClazzRefProvider.class);
		authMethodsRefValues = authContextClazzRefProvider.getAuthMethodsRefValues();
	}

	/**
	 * Decipher request.
	 *
	 * @param requestBody the request body
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Override
	protected Map<String, Object> decipherRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			if (null != requestBody.get(REQUEST)) {
				requestBody.replace(REQUEST, decode((String) requestBody.get(REQUEST)));
				if (null == requestBody.get(REQUEST_HMAC)) {
					throwMissingInputParameter(REQUEST_HMAC);
				} else {
					requestBody.replace(REQUEST_HMAC, decode((String) requestBody.get(REQUEST_HMAC)));
					byte[] encryptedSessionkey = (byte[]) decode((String) requestBody.get(REQUEST_SESSION_KEY));
					byte[] encryptedHmac = (byte[]) requestBody.get(REQUEST_HMAC);
					String thumbprint = Objects.nonNull(requestBody.get(THUMBPRINT))
							? String.valueOf(requestBody.get(THUMBPRINT))
							: null;
					String reqHMAC = keyManager.kernelDecryptAndDecode(thumbprint, encryptedSessionkey, encryptedHmac, fetchReferenceId(),
							isThumbprintValidationRequired());
					Map<String, Object> request = keyManager.requestData(requestBody, mapper, fetchReferenceId(),
							thumbprint, isThumbprintValidationRequired(),
							requestData -> validateRequestHMAC(reqHMAC, requestData));

					// If biometrics is present validate and decipher it.
					if (request.get(BIOMETRICS) != null) {
						
						decipherBioData(request);
						
						if(!isBiometricHashValidationDisabled()) {
							validateHashWithDecryptedBdbInRequest(request);
						}
					}
					
					requestBody.replace(REQUEST, request);

				}

			}

			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> processDecipheredReqeuest(Map<String, Object> decipheredRequest) {
		Map<String, Object> request = (Map<String, Object>) decipheredRequest.get(REQUEST);
		if (request != null && request.get(DEMOGRAPHICS) instanceof Map) {
			setDymanicDemograpicData((Map<String, Object>) request.get(DEMOGRAPHICS));
		}
		return decipheredRequest;
	}

	/**
	 * Checks if is hash based on biometric data block.
	 *
	 * @return true, if is hash based on biometric data block
	 */
	protected boolean isBiometricHashValidationDisabled() {
		return EnvUtil.getIsBioHashValidationDisabled();
	}

	/**
	 * Sets the dymanic demograpic data.
	 *
	 * @param demographics the demographics
	 */
	private void setDymanicDemograpicData(Map<String, Object> demographics) {
		Map<String, List<String>> dynamicAttributes = idMappingConfig.getDynamicAttributes();
		
		// First putting all demographics attributes which are mapped as dynamic in mapping
		// config
		Map<String, Object> metadata = demographics.entrySet()
												.stream()
												.filter(entry -> dynamicAttributes.containsKey(entry.getKey()))
												.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (m1, m2) -> m1 , () -> new LinkedHashMap<>()));
		
		Set<String> staticIdNames = Stream.of(IdaIdMapping.values())
											.map(IdaIdMapping::getIdname)
											.collect(Collectors.toSet());
		// Putting all demographics attributes which are not mapped in mapping config
		metadata.putAll(demographics.entrySet()
			.stream()
			.filter(entry -> !staticIdNames.contains(entry.getKey()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		
		demographics.put(METADATA, metadata);

	}

	/**
	 * Decipher bio data.
	 *
	 * @param request the request
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private void decipherBioData(Map<String, Object> request) throws IdAuthenticationAppException {
		Object biometrics = request.get(BIOMETRICS);
		if (Objects.nonNull(biometrics) && biometrics instanceof List) {
			List<Object> bioIdentity = (List<Object>) biometrics;
			List<Object> bioIdentityInfo = new ArrayList<>();
			for (int i = 0; i < bioIdentity.size(); i++) {
				Object obj = bioIdentity.get(i);
				if (obj instanceof Map) {
					bioIdentityInfo.add(decipherBioData(obj, i));
				}
			}
			request.replace(BIOMETRICS, bioIdentityInfo);
		}
	}

	/**
	 * Decipher bio data.
	 *
	 * @param obj   the obj
	 * @param index the index
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> decipherBioData(Object obj, int index) throws IdAuthenticationAppException {

		Map<String, Object> map = (Map<String, Object>) obj;

		Optional<String> dataOpt = getStringValue(map, DATA);
		if (!dataOpt.isPresent()) {
			throwMissingInputParameter(String.format(BIO_DATA_INPUT_PARAM, index));
		}
		
		verifyBioDataSignature(dataOpt.get(), index);

		if (!getStringValue(map, SESSION_KEY).isPresent()) {
			throwMissingInputParameter(String.format(BIO_SESSIONKEY_INPUT_PARAM, index));
		}

		try {
			byte[] decodedData = CryptoUtil.decodeBase64Url(extractBioData((String) map.get(DATA)));
			Map<String, Object> data = mapper.readValue(decodedData, Map.class);

			if (!getStringValue(data, BIO_VALUE).isPresent()) {
				throwMissingInputParameter(String.format(BIO_VALUE_INPUT_PARAM, index));
			}

			if (!getStringValue(data, TIMESTAMP).isPresent()) {
				throwMissingInputParameter(String.format(BIO_TIMESTAMP_INPUT_PARAM, index));
			}

			Object bioValue = data.get(BIO_VALUE);

			Object jwsSignatureObj = data.get(DIGITAL_ID);

			if (jwsSignatureObj instanceof String) {
				String jwsSignature = (String) jwsSignatureObj;
				if (StringUtils.isNotEmpty(jwsSignature)) {
					verifyDigitalIdSignature(jwsSignature, index);
					data.replace(DIGITAL_ID, decipherDigitalId(jwsSignature));
				}
			}

			Object sessionKey = Objects.nonNull(map.get(SESSION_KEY)) ? map.get(SESSION_KEY) : null;
			Object thumbprint = Objects.nonNull(map.get(THUMBPRINT)) ? map.get(THUMBPRINT) : null;
			String timestamp = String.valueOf(data.get(TIMESTAMP));
			String transactionId = String.valueOf(data.get(TRANSACTION_ID));
			byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
			byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, EnvUtil.getSaltLastBytesSum());
			String salt = CryptoUtil.encodeBase64(saltLastBytes);
			byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, EnvUtil.getAadLastBytesSum());
			String aad = CryptoUtil.encodeBase64(aadLastBytes);
			String decryptedData = keyManager.kernelDecrypt(String.valueOf(thumbprint), CryptoUtil.decodeBase64Url(String.valueOf(sessionKey)), CryptoUtil.decodeBase64Url(String.valueOf(bioValue)), getBioRefId(),
					aad, salt, isThumbprintValidationRequired());
			data.replace(BIO_VALUE, decryptedData);
			map.replace(DATA, data);
			return map;
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Gets the bio ref id.
	 *
	 * @return the bio ref id
	 */
	protected String getBioRefId() {
		return EnvUtil.getPartnerBioRefId();
	}

	/**
	 * This method validates the digitalID signature.
	 *
	 * @param jwsSignature the jws signature
	 * @param index 
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void verifyDigitalIdSignature(String jwsSignature, int index) throws IdAuthenticationAppException {
		if (!verifySignature(jwsSignature, null, DomainType.DIGITAL_ID.getType())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "verifyDigitalIdSignature", "Invalid certificate in biometrics>data>digitalId");
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.DSIGN_FALIED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DSIGN_FALIED.getErrorMessage(),
							"request/biometrics/" + index + "/data/digitalId"));
		}
	}

	/**
	 * This method deciphers the digitalId from jws.
	 *
	 * @param jwsSignature the jws signature
	 * @return the digital id
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected DigitalId decipherDigitalId(String jwsSignature) throws IdAuthenticationAppException {
		try {
			return mapper.readValue(CryptoUtil.decodeBase64Url(getPayloadFromJwsSingature(jwsSignature)), DigitalId.class);
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Method to get the reference id.
	 *
	 * @return the string
	 */
	protected String fetchReferenceId() {
		return EnvUtil.getPartnerRefId();
	}

	/**
	 * Validate deciphered request.
	 *
	 * @param requestWrapper the request wrapper
	 * @param requestBody the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#
	 * validateDecipheredRequest(io.mosip.authentication.service.filter.
	 * ResettableStreamHttpServletRequest, java.util.Map)
	 */
	@Override
	protected void validateDecipheredRequest(ResettableStreamHttpServletRequest requestWrapper,
			Map<String, Object> requestBody) throws IdAuthenticationAppException {
		
		String headerCertificateThumbprint = getCertificateThumbprintFromSignatureData(requestWrapper.getHeader("signature"));
		Map<String, String> partnerLkMap = getAuthPart(requestWrapper);
		
		String partnerId = partnerLkMap.get(PARTNER_ID);
		String licenseKey = partnerLkMap.get(MISPLICENSE_KEY);
		String partnerApiKey = partnerLkMap.get(API_KEY);

		if (partnerId != null && licenseKey != null) {
			PartnerPolicyResponseDTO partnerServiceResponse = getPartnerPolicyInfo(partnerId, partnerApiKey, licenseKey,
					isPartnerCertificateNeeded(), headerCertificateThumbprint, isCertificateValidationRequired());
			// First, validate MISP Policy.
			checkMispPolicyAllowed(partnerServiceResponse);
			// Second, validate the auth policy attributes.
			checkAllowedAuthTypeBasedOnPolicy(partnerServiceResponse, requestBody);
			// Later, Validate OIDC Client allowed AMR values.
			checkAllowedAMRBasedOnClientConfig(requestBody, partnerServiceResponse);
			addMetadata(requestBody, partnerId, partnerApiKey, partnerServiceResponse,
					partnerServiceResponse.getCertificateData());
		}
	}

	/**
	 * Checks if is partner certificate needed.
	 *
	 * @return true, if is partner certificate needed
	 */
	protected boolean isPartnerCertificateNeeded() {
		return false;
	}

	/**
	 * Gets the partner policy info.
	 *
	 * @param partnerId the partner id
	 * @param partnerApiKey the partner api key
	 * @param licenseKey the license key
	 * @param certificateNeeded the certificate needed
	 * @return the partner policy info
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private PartnerPolicyResponseDTO getPartnerPolicyInfo(String partnerId, String partnerApiKey, String licenseKey,
			boolean certificateNeeded, String signatureHeaderCertificate, boolean certValidationNeeded) throws IdAuthenticationAppException {
		try {
			return partnerService.validateAndGetPolicy(partnerId, partnerApiKey, licenseKey, certificateNeeded, 
						signatureHeaderCertificate, certValidationNeeded);
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}


	@SuppressWarnings("unchecked")
	private String getCertificateThumbprintFromSignatureData(String signatureData) throws IdAuthenticationAppException {

		if (Objects.isNull(signatureData)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_CERTIFICATE_NOT_FOUND_IN_REQ_HEADER.getErrorCode(), 
				IdAuthenticationErrorConstants.PARTNER_CERTIFICATE_NOT_FOUND_IN_REQ_HEADER.getErrorMessage());
		}
		String[] signatureTokens = signatureData.split(PERIOD, -1);
		String jwtTokenHeader = new String(CryptoUtil.decodeBase64Url(signatureTokens[0]));
		Map<String, Object> jwtTokenHeadersMap = null;
		try {
			jwtTokenHeadersMap = JsonUtils.jsonStringToJavaMap(jwtTokenHeader);
			if (jwtTokenHeadersMap.containsKey(JWT_HEADER_CERT_KEY)) {
				List<String> certList = (List<String>) jwtTokenHeadersMap.get(JWT_HEADER_CERT_KEY);
				// Decoding and url safe encoding because parsed header certificate is returing without url safe encoding.
				byte[] certData = Base64.decodeBase64(certList.get(0));
				CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			 	X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certData));
				return DigestUtils.sha256Hex(x509Cert.getEncoded()).toUpperCase();
				//return CryptoUtil.encodeBase64Url(getPEMFormatedData(x509Cert).getBytes());
			}
		} catch (JsonParseException | JsonMappingException | io.mosip.kernel.core.exception.IOException | CertificateException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "ParseHeader", 
				"Error Getting certificate from signature header.");
		} 
		// This not never happen, because certificate comparison will be done after successful signature validation. 
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_CERTIFICATE_NOT_FOUND_IN_REQ_HEADER.getErrorCode(), 
			IdAuthenticationErrorConstants.PARTNER_CERTIFICATE_NOT_FOUND_IN_REQ_HEADER.getErrorMessage());
	}

	/**
	 * Adds the metadata.
	 *
	 * @param requestBody the request body
	 * @param partnerId the partner id
	 * @param partnerApiKey the partner api key
	 * @param partnerServiceResponse the partner service response
	 * @param partnerCertificate the partner certificate
	 */
	private void addMetadata(Map<String, Object> requestBody, String partnerId, String partnerApiKey,
			PartnerPolicyResponseDTO partnerServiceResponse, String partnerCertificate) {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("partnerId", partnerId);
		metadata.put(partnerId, createPartnerDTO(partnerServiceResponse, partnerApiKey));
		metadata.put(partnerId + partnerApiKey, partnerServiceResponse);
		metadata.put(IdAuthCommonConstants.KYC_LANGUAGES, validateAndGetKycLanguages(partnerServiceResponse.getPolicy().getKycLanguages()));
		if (partnerCertificate != null) {
			metadata.put(IdAuthCommonConstants.PARTNER_CERTIFICATE, partnerCertificate);
		}
		requestBody.put(METADATA, metadata);
	}
	
	/**
	 * Validates the kyc languages from policy.
	 * Expecting at least one language should be supported by the system. 
	 * Not matches found returns system supported languages.
	 * @param kycLanguages
	 * @return
	 */
	private Set<String> validateAndGetKycLanguages(Set<String> kycLanguages) {
		Set<String> systemSupportedLanguages = getSystemSupportedLanguageCodes();
		if(kycLanguages != null && kycLanguages.stream().anyMatch(systemSupportedLanguages::contains)) {
			return kycLanguages;
		}
		return systemSupportedLanguages;
	}
	/**
	 * Creates the partner DTO.
	 *
	 * @param partnerPolicyDTO the partner policy DTO
	 * @param partnerApiKey the partner api key
	 * @return the partner DTO
	 */
	private PartnerDTO createPartnerDTO(PartnerPolicyResponseDTO partnerPolicyDTO, String partnerApiKey) {
		PartnerDTO partnerDTO = new PartnerDTO();
		partnerDTO.setPartnerId(partnerPolicyDTO.getPartnerId());
		partnerDTO.setPartnerApiKey(partnerApiKey);
		partnerDTO.setPartnerName(partnerPolicyDTO.getPartnerName());
		partnerDTO.setPolicyId(partnerPolicyDTO.getPolicyId());
		partnerDTO.setStatus("Active");
		return partnerDTO;
	}

	/**
	 * Validate bio data in request.
	 *
	 * @param requestBody the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void validateHashWithDecryptedBdbInRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		validateHashWithDecryptedBdbInSegments(getBiometricsSegmentsList(requestBody));
	}

	/**
	 * Gets the biometrics segments list.
	 *
	 * @param requestBody the request body
	 * @return the biometrics segments list
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getBiometricsSegmentsList(Map<String, Object> requestBody) {
		List<Map<String, Object>> biometricsList = Optional.ofNullable(requestBody.get(BIOMETRICS))
				.filter(obj -> obj instanceof List).map(obj -> (List<Map<String, Object>>) obj)
				.orElse(Collections.emptyList());
		return biometricsList;
	}

	/**
	 * Validate bio data.
	 *
	 * @param biometricsList the biometrics list
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private void validateHashWithDecryptedBdbInSegments(List<Map<String, Object>> biometricsList) throws IdAuthenticationAppException {
		try {
			byte[] previousHash = getHash("");

			for (int i = 0; i < biometricsList.size(); i++) {
				Map<String, Object> biometricData = biometricsList.get(i);
				Map<String, Object> dataMap = (Map<String, Object>)biometricData.get(DATA);

				Optional<String> bioValueOpt = getStringValue(dataMap, BIO_VALUE);
				if (!bioValueOpt.isPresent()) {
					throwMissingInputParameter(String.format(BIO_VALUE_INPUT_PARAM, i));
				}

				Optional<String> hashOpt = getStringValue(biometricData, HASH);

				if (!hashOpt.isPresent()) {
					throwMissingInputParameter(String.format(HASH_INPUT_PARAM, i));
				}

				byte[] bdb = CryptoUtil.decodeBase64(bioValueOpt.get());

				previousHash = validateHashBytes(bdb, hashOpt.get(), previousHash);
			}
		} catch (UnsupportedEncodingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	
	protected String extractBioData(String dataFieldValue) throws IdAuthenticationAppException {
		return getPayloadFromJwsSingature(dataFieldValue);
	}

	/**
	 * Digest.
	 *
	 * @param hash the hash
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private String digest(byte[] hash) throws IdAuthenticationAppException {
		return IdAuthSecurityManager.digestAsPlainText(hash);
	}

	/**
	 * Gets the hash.
	 *
	 * @param string the string
	 * @return the hash
	 * @throws UnsupportedEncodingException      the unsupported encoding exception
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private byte[] getHash(String string) throws UnsupportedEncodingException, IdAuthenticationAppException {
		return getHash(string.getBytes(UTF_8));
	}

	/**
	 * Gets the hash.
	 *
	 * @param bytes the bytes
	 * @return the hash
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private byte[] getHash(byte[] bytes) throws IdAuthenticationAppException {
		try {
			return IdAuthSecurityManager.generateHash(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Validate hash.
	 *
	 * @param bdb the bdb
	 * @param inputHashDigest the input hash digest
	 * @param previousHash  the previous hash
	 * @return the byte[]
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private byte[] validateHashBytes(byte[] bdb, String inputHashDigest, byte[] previousHash)
			throws IdAuthenticationAppException, UnsupportedEncodingException {
		byte[] currentHash = getHash(bdb);
		
		byte[] finalConcat = concatBytes(previousHash, currentHash);
		
		byte[] finalHash = getHash(finalConcat);
		String finalHashDigest = digest(finalHash);

		if (!inputHashDigest.equals(finalHashDigest)) {
			throwError(IdAuthenticationErrorConstants.INVALID_HASH);
		}

		return finalHash;

	}
	

	/**
	 * Contat bytes.
	 *
	 * @param previousHash the previous hash
	 * @param currentHash the current hash
	 * @return the byte[]
	 */
	private static byte[] concatBytes(byte[] previousHash, byte[] currentHash) {
		byte[] finalHash = new byte[currentHash.length + previousHash.length];
		System.arraycopy(previousHash, 0, finalHash, 0, previousHash.length);
		System.arraycopy(currentHash, 0, finalHash, previousHash.length, currentHash.length);
		return finalHash;
	}

	/**
	 * Throw missing input parameter.
	 *
	 * @param inputParam the input param
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void throwMissingInputParameter(String inputParam) throws IdAuthenticationAppException {
		throwError(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER, inputParam);
	}

	/**
	 * Throw error.
	 *
	 * @param errorConst the error const
	 * @param args       the args
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void throwError(IdAuthenticationErrorConstants errorConst, String... args)
			throws IdAuthenticationAppException {
		if (args != null && args.length > 0) {
			throw new IdAuthenticationAppException(errorConst.getErrorCode(),
					String.format(errorConst.getErrorMessage(), (Object[]) args));
		} else {
			throw new IdAuthenticationAppException(errorConst.getErrorCode(), errorConst.getErrorMessage());
		}
	}

	/**
	 * Gets the string value.
	 *
	 * @param map       the biometric data
	 * @param fieldName the field name
	 * @return the string value
	 */
	private Optional<String> getStringValue(Map<String, Object> map, String fieldName) {
		return Optional.ofNullable(map.get(fieldName)).filter(obj -> obj instanceof String).map(obj -> (String) obj);
	}

	private void checkMispPolicyAllowed(PartnerPolicyResponseDTO partnerPolicyResponseDTO) throws IdAuthenticationAppException {

		if (isMispPolicyValidationRequired()) {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "checkMispPolicyAllowed", 
					"MISP Policy Validation Required for the request.");
			MispPolicyDTO mispPolicy =  partnerPolicyResponseDTO.getMispPolicy();
			if (Objects.isNull(mispPolicy)) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "checkMispPolicyAllowed", 
						"MISP Policy not avaialble for the MISP partner.");
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.MISP_POLICY_NOT_FOUND.getErrorCode(), 
					IdAuthenticationErrorConstants.MISP_POLICY_NOT_FOUND.getErrorMessage());
			}
			// check whether policy is allowed or not for kyc-auth/kyc-exchange/key-binding.
            checkMispPolicyAllowed(mispPolicy);
			// TODO For KYC OTP request need to handle thru different filter. We will implement later.
		}
	}
	/**
	 * Check allowed auth type based on policy.
	 *
	 * @param partnerPolicyResponseDTO the partner policy response DTO
	 * @param requestBody the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void checkAllowedAuthTypeBasedOnPolicy(PartnerPolicyResponseDTO partnerPolicyResponseDTO, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		if (partnerPolicyResponseDTO != null) {
			List<AuthPolicy> authPolicies = partnerPolicyResponseDTO.getPolicy().getAllowedAuthTypes();
			List<KYCAttributes> allowedKycAttributes = partnerPolicyResponseDTO.getPolicy().getAllowedKycAttributes();
			List<String> allowedAttibuteNameList = Optional.ofNullable(allowedKycAttributes).stream()
					.flatMap(Collection::stream).map(KYCAttributes::getAttributeName).collect(Collectors.toList());
			requestBody.put("allowedKycAttributes", allowedAttibuteNameList);
			checkAllowedAuthTypeBasedOnPolicy(requestBody, authPolicies);
			List<AuthPolicy> mandatoryAuthPolicies = authPolicies.stream().filter(AuthPolicy::isMandatory)
					.collect(Collectors.toList());
			checkMandatoryAuthTypeBasedOnPolicy(requestBody, mandatoryAuthPolicies);
		} else {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_POLICY_ID);
		}
	}

	/**
	 * Check allowed auth type for bio based on the policies.
	 *
	 * @param requestBody  the request body
	 * @param authPolicies the auth policies
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException {
		try {
			AuthRequestDTO authRequestDTO = mapper.readValue(mapper.writeValueAsBytes(requestBody),
					AuthRequestDTO.class);
			if (AuthTypeUtil.isDemo(authRequestDTO) && !isAllowedAuthType(MatchType.Category.DEMO.getType(), authPolicies)) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
								MatchType.Category.DEMO.name()));
			}

			if (AuthTypeUtil.isBio(authRequestDTO)) {
				checkAllowedAuthTypeForBio(requestBody, authPolicies);
			}

			if (AuthTypeUtil.isPin(authRequestDTO) && !isAllowedAuthType(MatchType.Category.SPIN.getType(), authPolicies)) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
								MatchType.Category.SPIN.name()));
			}
			if (AuthTypeUtil.isOtp(authRequestDTO) && !isAllowedAuthType(MatchType.Category.OTP.getType(), authPolicies)) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
								MatchType.Category.OTP.name()));
			}
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Check allowed auth type for bio.
	 *
	 * @param requestBody  the request body
	 * @param authPolicies the auth policies
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IOException                  Signals that an I/O exception has
	 *                                      occurred.
	 */
	@SuppressWarnings("unchecked")
	private void checkAllowedAuthTypeForBio(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException, IOException {

		Object value = Optional.ofNullable(requestBody.get(IdAuthCommonConstants.REQUEST))
				.filter(obj -> obj instanceof Map).map(obj -> ((Map<String, Object>) obj).get(BIOMETRICS))
				.filter(obj -> obj instanceof List).orElse(Collections.emptyList());
		List<BioIdentityInfoDTO> listBioInfo = mapper.readValue(mapper.writeValueAsBytes(value),
				new TypeReference<List<BioIdentityInfoDTO>>() {
				});

		OptionalInt noBioTypeIndex = IntStream.range(0, listBioInfo.size()).filter(i -> {
			BioIdentityInfoDTO bioIdInfoDto = listBioInfo.get(i);
			return Objects.nonNull(bioIdInfoDto.getData()) && StringUtils.isEmpty(bioIdInfoDto.getData().getBioType());
		}).findFirst();
		if (noBioTypeIndex.isPresent()) {
			throwMissingInputParameter(String.format(BIO_TYPE_INPUT_PARAM, noBioTypeIndex.getAsInt()));
		}

		OptionalInt nodeviceTypeIndex = IntStream.range(0, listBioInfo.size()).filter(i -> {
			BioIdentityInfoDTO bioIdInfoDto = listBioInfo.get(i);
			return Objects.nonNull(bioIdInfoDto.getData())
					&& Objects.nonNull(bioIdInfoDto.getData().getDigitalId())
					&& StringUtils.isEmpty(bioIdInfoDto.getData().getDigitalId().getType());
		}).findFirst();

		if (nodeviceTypeIndex.isPresent()) {
			throwMissingInputParameter(String.format(BIO_DIGITALID_INPUT_PARAM_TYPE, nodeviceTypeIndex.getAsInt()));
		}

		List<String> bioTypeList = listBioInfo.stream().map(s -> s.getData().getBioType()).collect(Collectors.toList());

		List<String> deviceTypeList = listBioInfo.stream().map(s -> s.getData().getDigitalId().getType())
				.collect(Collectors.toList());

		if (bioTypeList.isEmpty()) {
			if (!isAllowedAuthType(MatchType.Category.BIO.getType(), authPolicies)) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), "bio"));
			}
		} else {
			checkAllowedAuthTypeForBio(authPolicies, bioTypeList, deviceTypeList);
		}
	}

	protected void checkAllowedAuthTypeForKeyBindedToken(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException, IOException {

		Object value = Optional.ofNullable(requestBody.get(IdAuthCommonConstants.REQUEST))
				.filter(obj -> obj instanceof Map).map(obj -> ((Map<String, Object>) obj).get(KEY_BINDED_TOKEN))
				.filter(obj -> obj instanceof List).orElse(Collections.emptyMap());

		List<KeyBindedTokenDTO> list = mapper.readValue(mapper.writeValueAsBytes(value),
				new TypeReference<List<KeyBindedTokenDTO>>() {
				});

		if(CollectionUtils.isEmpty(list)) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), "keybindedtoken"));
		}

		//TODO need to check all the elements in the list instead of only first element
		if (!isAllowedAuthType(MatchType.Category.KBT.getType(), null, authPolicies)) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(), String.format(
					IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), MatchType.Category.KBT.getType()));
		}
	}

	protected void checkAllowedAuthTypeForPassword(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException, IOException {
		KycAuthRequestDTO authRequestDTO = mapper.readValue(mapper.writeValueAsBytes(requestBody),
					KycAuthRequestDTO.class);

		if (AuthTypeUtil.isPassword(authRequestDTO) && !isAllowedAuthType(MatchType.Category.PWD.getType(), authPolicies)) {
			throw new IdAuthenticationAppException(
			IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
			String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
					MatchType.Category.PWD.name()));
		}
	}

	/**
	 * Check allowed auth type for bio.
	 *
	 * @param authPolicies the auth policies
	 * @param bioTypeList  the bio type list
	 * @param deviceTypeList the device type list
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void checkAllowedAuthTypeForBio(List<AuthPolicy> authPolicies, List<String> bioTypeList,
			List<String> deviceTypeList) throws IdAuthenticationAppException {
		String bioAuthType;
		for (String bioType : bioTypeList) {
			bioAuthType = bioType;
			if (bioType.equalsIgnoreCase(BioAuthType.FGR_IMG.getType())
					|| (EnvUtil.getIsFmrEnabled() && bioType.equalsIgnoreCase(BioAuthType.FGR_MIN.getType()))) {
				bioType = BiometricType.FINGER.value();
			} else if (bioType.equalsIgnoreCase(BioAuthType.FACE_IMG.getType())) {
				bioType = BiometricType.FACE.value();
			} else if (bioType.equalsIgnoreCase(BioAuthType.IRIS_IMG.getType())) {
				bioType = BiometricType.IRIS.value();
			}

			if (!isDeviceTypeBioTypeSame(bioType, deviceTypeList)) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.DEVICE_TYPE_BIO_TYPE_NOT_MATCH.getErrorCode(),
						IdAuthenticationErrorConstants.DEVICE_TYPE_BIO_TYPE_NOT_MATCH.getErrorMessage());
			}

			if (!isAllowedAuthType(MatchType.Category.BIO.getType(), bioType, authPolicies)) {
				if (!BioAuthType.getSingleBioAuthTypeForType(bioAuthType).isPresent()) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
									BIO_TYPE));
				}
				String bioSubtype = MatchType.Category.BIO.name() + "-" + bioAuthType;
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(), String.format(
								IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), bioSubtype));
			}
		}
	}

	/**
	 * Checks if is device type bio type same.
	 *
	 * @param bioType the bio type
	 * @param deviceTypeList the device type list
	 * @return true, if is device type bio type same
	 */
	private boolean isDeviceTypeBioTypeSame(String bioType, List<String> deviceTypeList) {
		return deviceTypeList.stream().anyMatch(subType -> subType.equalsIgnoreCase(bioType));
	}

	/**
	 * Check mandatory auth type based on policy.
	 *
	 * @param requestBody           the request body
	 * @param mandatoryAuthPolicies the mandatory auth policies
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	protected void checkMandatoryAuthTypeBasedOnPolicy(Map<String, Object> requestBody,
			List<AuthPolicy> mandatoryAuthPolicies) throws IdAuthenticationAppException {
		try {
			AuthRequestDTO authRequestDto = mapper.readValue(mapper.writeValueAsBytes(requestBody),
					AuthRequestDTO.class);
			Object value = Optional.ofNullable(requestBody.get(IdAuthCommonConstants.REQUEST))
					.filter(Map.class::isInstance).map(obj -> ((Map<String, Object>) obj).get(BIOMETRICS))
					.filter(List.class::isInstance).orElse(Collections.emptyList());
			List<BioIdentityInfoDTO> listBioInfo = mapper.readValue(mapper.writeValueAsBytes(value),
					new TypeReference<List<BioIdentityInfoDTO>>() {
					});
			List<String> bioTypeList = listBioInfo.stream().map(s -> s.getData().getBioType().toUpperCase())
					.collect(Collectors.toList());
			for (AuthPolicy mandatoryAuthPolicy : mandatoryAuthPolicies) {
				validateAuthPolicy(requestBody, authRequestDto, bioTypeList, mandatoryAuthPolicy);
			}
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Validate auth type allowed through auth policies.
	 *
	 * @param requestBody         the request body
	 * @param authType            the auth type
	 * @param bioTypeList         the bio type list
	 * @param mandatoryAuthPolicy the mandatory auth policy
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void validateAuthPolicy(Map<String, Object> requestBody, AuthRequestDTO authRequestDTO, List<String> bioTypeList,
			AuthPolicy mandatoryAuthPolicy) throws IdAuthenticationAppException {
		if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.OTP.getType()) && 
				!AuthTypeUtil.isOtp(authRequestDTO)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
							MatchType.Category.OTP.getType()));
		} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.DEMO.getType())
				&& !AuthTypeUtil.isDemo(authRequestDTO)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
							MatchType.Category.DEMO.getType()));
		} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.SPIN.getType())
				&& !AuthTypeUtil.isPin(authRequestDTO)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
							MatchType.Category.SPIN.getType()));
		} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.BIO.getType())) {
			if (!AuthTypeUtil.isBio(authRequestDTO)) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
								MatchType.Category.BIO.getType()));
			} else {
				if (!bioTypeList.contains(mandatoryAuthPolicy.getAuthSubType().toUpperCase())) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
									MatchType.Category.BIO.getType() + "-" + mandatoryAuthPolicy.getAuthSubType()));
				}
			}
		} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(KYC)
				&& !Optional.ofNullable(requestBody.get("id"))
						.filter(id -> id.equals(EnvUtil.getIdaApiIdWithKyc()))
						.isPresent()) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(), KYC));
		}
	}

	/**
	 * Checks if is allowed auth type.
	 *
	 * @param authType the auth type
	 * @param policies the policies
	 * @return true, if is allowed auth type
	 */
	protected boolean isAllowedAuthType(String authType, List<AuthPolicy> policies) {
		return isAllowedAuthType(authType, null, policies);
	}

	/**
	 * Checks if is allowed auth type.
	 *
	 * @param authType    the auth type
	 * @param subAuthType the sub auth type
	 * @param policies    the policies
	 * @return true, if is allowed auth type
	 */
	protected boolean isAllowedAuthType(String authType, String subAuthType, List<AuthPolicy> policies) {
		if (subAuthType == null) {
			return policies.stream().anyMatch(authPolicy -> authPolicy.getAuthType().equalsIgnoreCase(authType));
		} else {
			return policies.stream().anyMatch(authPolicy -> authPolicy.getAuthType().equalsIgnoreCase(authType)
					&& authPolicy.getAuthSubType().equalsIgnoreCase(subAuthType));
		}
	}

	private void checkAllowedAMRBasedOnClientConfig(Map<String, Object> requestBody, PartnerPolicyResponseDTO partnerPolicyResponseDTO) 
			throws IdAuthenticationAppException {
		try {
			if (isAMRValidationRequired()) {
				Set<String> allowedAMRs = getAuthenticationFactors(partnerPolicyResponseDTO);
				AuthRequestDTO authRequestDTO = mapper.readValue(mapper.writeValueAsBytes(requestBody),
							AuthRequestDTO.class);
				if (AuthTypeUtil.isDemo(authRequestDTO) && !allowedAMRs.contains(MatchType.Category.DEMO.getType())) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
									MatchType.Category.DEMO.name()));
				}
				if (AuthTypeUtil.isBio(authRequestDTO) && !allowedAMRs.contains(MatchType.Category.BIO.getType())) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
									MatchType.Category.BIO.name()));
				}
	
				if (AuthTypeUtil.isPin(authRequestDTO)  && !allowedAMRs.contains(MatchType.Category.SPIN.getType())) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
									MatchType.Category.SPIN.name()));
				}
				if (AuthTypeUtil.isOtp(authRequestDTO)  && !allowedAMRs.contains(MatchType.Category.OTP.getType())) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
									MatchType.Category.OTP.name()));
				}

				KycAuthRequestDTO kycAuthRequestDTO = mapper.readValue(mapper.writeValueAsBytes(requestBody),
										KycAuthRequestDTO.class);
				if (AuthTypeUtil.isPassword(kycAuthRequestDTO)  && !allowedAMRs.contains(MatchType.Category.PWD.getType())) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
									MatchType.Category.PWD.name()));
				}
				checkAllowedAMRForKBT(requestBody, allowedAMRs);
			}
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	protected void checkAllowedAMRForKeyBindedToken(Map<String, Object> requestBody, Set<String> allowedAMRs)
			throws IdAuthenticationAppException, IOException {

		Object value = Optional.ofNullable(requestBody.get(IdAuthCommonConstants.REQUEST))
				.filter(obj -> obj instanceof Map).map(obj -> ((Map<String, Object>) obj).get(KEY_BINDED_TOKEN))
				.filter(obj -> obj instanceof List).orElse(Collections.emptyMap());
				
		List<KeyBindedTokenDTO> list = mapper.readValue(mapper.writeValueAsBytes(value),
				new TypeReference<List<KeyBindedTokenDTO>>() {
				});

		if(CollectionUtils.isEmpty(list)) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), "keybindedtoken"));
		}

		Set<String> amrInRequest = list.stream()
				.filter( kbt -> !org.springframework.util.StringUtils.isEmpty(kbt.getType()))
				.map(KeyBindedTokenDTO::getType)
				.map(String::toLowerCase)
				.collect(Collectors.toSet());

		if (!allowedAMRs.containsAll(amrInRequest)) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(), String.format(
					IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), list.get(0).getType()));
		}
	}

	private Set<String> getAuthenticationFactors(PartnerPolicyResponseDTO partnerPolicyResponseDTO) {

		Set<String> clientConfiguredAMRs = Stream.of(partnerPolicyResponseDTO.getOidcClientDto().getAuthContextRefs()).collect(Collectors.toSet());
		
		Map<String, List<AuthenticationFactor>> allowedAMRs = authMethodsRefValues.getAuthMethodsRefValues();
		Set<String> filterAMRs = new HashSet<>();
		for (String key: allowedAMRs.keySet()) {
			if (clientConfiguredAMRs.contains(key)) {
				List<AuthenticationFactor> amrs = allowedAMRs.get(key);
				// not considering count in AuthenticationFactor. Need to handle later.
				for (AuthenticationFactor amr : amrs) {
					if (Objects.nonNull(amr.getSubTypes())) {
						filterAMRs.addAll(amr.getSubTypes().stream()
										 .filter( subtype -> !org.springframework.util.StringUtils.isEmpty(subtype))
				 						 .map(String::toLowerCase)
										 .collect(Collectors.toSet()));
					}
					filterAMRs.add(amr.getType().toLowerCase());
				}
			}
		}
		return filterAMRs;
	}

	/**
	 * Gets the auth part.
	 *
	 * @param requestWrapper the request wrapper
	 * @return the auth part
	 */
	protected Map<String, String> getAuthPart(ResettableStreamHttpServletRequest requestWrapper) throws IdAuthenticationAppException{
		Map<String, String> params = new HashMap<>();
		String url = requestWrapper.getRequestURL().toString();
		String contextPath = requestWrapper.getContextPath();
		if ((Objects.nonNull(url) && !url.isEmpty()) && (Objects.nonNull(contextPath) && !contextPath.isEmpty())) {
			String[] splitedUrlByContext = url.split(contextPath);
			String[] paramsArray = Stream.of(splitedUrlByContext[1].split("/")).filter(str -> !str.isEmpty())
									.toArray(size -> new String[size]);
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "getAuthPart", 
					"List of Path Parameters received in url: " + Stream.of(paramsArray).collect(Collectors.joining(", ")));

			if (paramsArray.length >= 3) {
				params.put(MISPLICENSE_KEY, paramsArray[paramsArray.length - 3]);
				params.put(PARTNER_ID, paramsArray[paramsArray.length - 2]);
				params.put(API_KEY, paramsArray[paramsArray.length - 1]);
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "getAuthPart", 
					"Required Number of Path Parameters are not available in URL.");
				throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.URI_PATH_PARAMS_MISSING.getErrorCode(), 
					IdAuthenticationErrorConstants.URI_PATH_PARAMS_MISSING.getErrorMessage());
					
			}
		}
		return params;
	}

	/**
	 * Checks if is signing required.
	 *
	 * @return true, if is signing required
	 */
	@Override
	protected boolean isSigningRequired() {
		return true;
	}

	/**
	 * Checks if is signature verification required.
	 *
	 * @return true, if is signature verification required
	 */
	@Override
	protected boolean isSignatureVerificationRequired() {
		return true;
	}

	/**
	 * Checks if is thumbprint validation required.
	 *
	 * @return true, if is thumbprint validation required
	 */
	@Override
	protected final boolean isThumbprintValidationRequired() {
		//return env.getProperty("mosip.ida.auth.thumbprint-validation-required", Boolean.class, true);
		//After integration with 1.1.5.1 version of keymanager, thumbprint is always mandated for decryption.
		return true;
	}

	@Override
	protected void checkMispPolicyAllowed(MispPolicyDTO mispPolicy) throws IdAuthenticationAppException {
        // Nothing required, Ignoring for other filters.
    }

	@Override
	protected void checkAllowedAMRForKBT(Map<String, Object> requestBody, Set<String> allowedAMRs) 
		throws IdAuthenticationAppException {
		// Nothing required, Ignoring for other filters.
	}

	/**
	 * Checks if is trust validation required.
	 *
	 * @return true, if is trust validation required
	 */
	@Override
	protected boolean isTrustValidationRequired() {
		return true;
	}
	
	/**
	 * Gets the system supported languages
	 * @return
	 */
	public Set<String> getSystemSupportedLanguageCodes() {
		String languages = EnvUtil.getMandatoryLanguages() + ","
				+ EnvUtil.getOptionalLanguages();		
		return new HashSet<>(List.of(languages.split(",")));
	}

}
