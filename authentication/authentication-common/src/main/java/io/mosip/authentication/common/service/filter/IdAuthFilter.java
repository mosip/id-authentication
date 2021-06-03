package io.mosip.authentication.common.service.filter;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.API_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIOMETRICS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_DATA_INPUT_PARAM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_DIGITALID_INPUT_PARAM_TYPE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_SESSIONKEY_INPUT_PARAM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_TIMESTAMP_INPUT_PARAM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_TYPE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_TYPE_INPUT_PARAM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_VALUE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_VALUE_INPUT_PARAM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DATA;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_AAD_LAST_BYTES_NUM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_SALT_LAST_BYTES_NUM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEMOGRAPHICS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DIGITAL_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.HASH;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.HASH_INPUT_PARAM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.KYC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.METADATA;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISPLICENSE_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST_HMAC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST_SESSION_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TIMESTAMP;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_BIO_HASH_VALIDATION_DISABLED;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.DomainType;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.KYCAttributes;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.BytesUtil;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.StringUtils;

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
public class IdAuthFilter extends BaseAuthFilter {
	
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthFilter.class);

	/** The Constant THUMBPRINT. */
	private static final String THUMBPRINT = "thumbprint";
	
	/** The Constant TRANSACTION_ID. */
	private static final String TRANSACTION_ID = "transactionId";
	
	/** The partner service. */
	protected PartnerService partnerService;
	
	/** The id mapping config. */
	private IDAMappingConfig idMappingConfig;
	
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
	}

	/**
	 * Decipher request.
	 *
	 * @param requestBody the request body
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
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
					
					if (request.get(DEMOGRAPHICS) instanceof Map) {
						setDymanicDemograpicData((Map<String, Object>) request.get(DEMOGRAPHICS));
					}

					requestBody.replace(REQUEST, request);

				}

			}

			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Checks if is hash based on biometric data block.
	 *
	 * @return true, if is hash based on biometric data block
	 */
	private boolean isBiometricHashValidationDisabled() {
		return env.getProperty(IDA_BIO_HASH_VALIDATION_DISABLED, Boolean.class, false);
	}

	/**
	 * Sets the dymanic demograpic data.
	 *
	 * @param demographics the demographics
	 */
	private void setDymanicDemograpicData(Map<String, Object> demographics) {
		Map<String, List<String>> dynamicAttributes = idMappingConfig.getDynamicAttributes();
		Map<String, Object> metadata = demographics.entrySet()
												.stream()
												.filter(entry -> dynamicAttributes.containsKey(entry.getKey()))
												.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
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
		
		verifyBioDataSignature(dataOpt.get());

		if (!getStringValue(map, SESSION_KEY).isPresent()) {
			throwMissingInputParameter(String.format(BIO_SESSIONKEY_INPUT_PARAM, index));
		}

		try {
			byte[] decodedData = CryptoUtil.decodeBase64(extractBioData((String) map.get(DATA)));
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
					verifyDigitalIdSignature(jwsSignature);
					data.replace(DIGITAL_ID, decipherDigitalId(jwsSignature));
				}
			}

			Object sessionKey = Objects.nonNull(map.get(SESSION_KEY)) ? map.get(SESSION_KEY) : null;
			Object thumbprint = Objects.nonNull(map.get(THUMBPRINT)) ? map.get(THUMBPRINT) : null;
			String timestamp = String.valueOf(data.get(TIMESTAMP));
			String transactionId = String.valueOf(data.get(TRANSACTION_ID));
			byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
			byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(
					IdAuthConfigKeyConstants.IDA_SALT_LASTBYTES_NUM, Integer.class, DEFAULT_SALT_LAST_BYTES_NUM));
			String salt = CryptoUtil.encodeBase64(saltLastBytes);
			byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(
					IdAuthConfigKeyConstants.IDA_AAD_LASTBYTES_NUM, Integer.class, DEFAULT_AAD_LAST_BYTES_NUM));
			String aad = CryptoUtil.encodeBase64(aadLastBytes);
			String decryptedData = keyManager.kernelDecrypt(String.valueOf(thumbprint), CryptoUtil.decodeBase64(String.valueOf(sessionKey)), CryptoUtil.decodeBase64(String.valueOf(bioValue)), getBioRefId(),
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
		return env.getProperty(IdAuthConfigKeyConstants.PARTNER_BIO_REFERENCE_ID);
	}

	/**
	 * This method validates the digitalID signature.
	 *
	 * @param jwsSignature the jws signature
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void verifyDigitalIdSignature(String jwsSignature) throws IdAuthenticationAppException {
		if (!verifySignature(jwsSignature, null, DomainType.DIGITAL_ID.getType())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "verifyDigitalIdSignature", "Invalid certificate in biometrics>data>digitalId");
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_CERTIFICATE);
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
			return mapper.readValue(CryptoUtil.decodeBase64(getPayloadFromJwsSingature(jwsSignature)), DigitalId.class);
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
		return env.getProperty(IdAuthConfigKeyConstants.PARTNER_REFERENCE_ID);
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
		Map<String, String> partnerLkMap = getAuthPart(requestWrapper);
		String partnerId = partnerLkMap.get(PARTNER_ID);
		String licenseKey = partnerLkMap.get(MISPLICENSE_KEY);
		String partnerApiKey = partnerLkMap.get(API_KEY);

		if (partnerId != null && licenseKey != null) {
			PartnerPolicyResponseDTO partnerServiceResponse = getPartnerPolicyInfo(partnerId, partnerApiKey, licenseKey,
					isPartnerCertificateNeeded());
			checkAllowedAuthTypeBasedOnPolicy(partnerServiceResponse, requestBody);
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
			boolean certificateNeeded) throws IdAuthenticationAppException {
		try {
			return partnerService.validateAndGetPolicy(partnerId, partnerApiKey, licenseKey, certificateNeeded);
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}
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
		metadata.put(partnerId + partnerApiKey, partnerServiceResponse.getPolicy());
		if (partnerCertificate != null) {
			metadata.put(IdAuthCommonConstants.PARTNER_CERTIFICATE, partnerCertificate);
		}
		requestBody.put(METADATA, metadata);
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
		
		byte[] finalHash = contatBytes(previousHash, currentHash);
		
		String finalHashDigest = digest(getHash(finalHash));

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
	private static byte[] contatBytes(byte[] previousHash, byte[] currentHash) {
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
			List<String> allowedTypeList = Optional.ofNullable(allowedKycAttributes).stream()
					.flatMap(Collection::stream).map(KYCAttributes::getAttributeName).collect(Collectors.toList());
			requestBody.put("allowedKycAttributes", allowedTypeList);
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
			AuthTypeDTO authType = mapper.readValue(mapper.writeValueAsBytes(requestBody.get("requestedAuth")),
					AuthTypeDTO.class);
			if (authType.isDemo() && !isAllowedAuthType(MatchType.Category.DEMO.getType(), authPolicies)) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
								MatchType.Category.DEMO.name()));
			}

			if (authType.isBio()) {
				checkAllowedAuthTypeForBio(requestBody, authPolicies);
			}

			if (authType.isPin() && !isAllowedAuthType(MatchType.Category.SPIN.getType(), authPolicies)) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
								MatchType.Category.SPIN.name()));
			}
			if (authType.isOtp() && !isAllowedAuthType(MatchType.Category.OTP.getType(), authPolicies)) {
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
					|| (FMR_ENABLED_TEST.test(env) && bioType.equalsIgnoreCase(BioAuthType.FGR_MIN.getType()))) {
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
			AuthTypeDTO authType = mapper.readValue(mapper.writeValueAsBytes(requestBody.get("requestedAuth")),
					AuthTypeDTO.class);
			Object value = Optional.ofNullable(requestBody.get(IdAuthCommonConstants.REQUEST))
					.filter(obj -> obj instanceof Map).map(obj -> ((Map<String, Object>) obj).get(BIOMETRICS))
					.filter(obj -> obj instanceof List).orElse(Collections.emptyList());
			List<BioIdentityInfoDTO> listBioInfo = mapper.readValue(mapper.writeValueAsBytes(value),
					new TypeReference<List<BioIdentityInfoDTO>>() {
					});
			List<String> bioTypeList = listBioInfo.stream().map(s -> s.getData().getBioType())
					.collect(Collectors.toList());
			if (bioTypeList.contains("Finger")) {
				bioTypeList.add("FINGER");
			}
			for (AuthPolicy mandatoryAuthPolicy : mandatoryAuthPolicies) {
				validateAuthPolicy(requestBody, authType, bioTypeList, mandatoryAuthPolicy);
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
	private void validateAuthPolicy(Map<String, Object> requestBody, AuthTypeDTO authType, List<String> bioTypeList,
			AuthPolicy mandatoryAuthPolicy) throws IdAuthenticationAppException {
		if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.OTP.getType()) && !authType.isOtp()) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
							MatchType.Category.OTP.getType()));
		} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.DEMO.getType())
				&& !authType.isDemo()) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
							MatchType.Category.DEMO.getType()));
		} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.SPIN.getType())
				&& !authType.isPin()) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
							MatchType.Category.SPIN.getType()));
		} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.BIO.getType())) {
			if (!authType.isBio()) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
								MatchType.Category.BIO.getType()));
			} else {
				if (!bioTypeList.contains(mandatoryAuthPolicy.getAuthSubType())) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
									MatchType.Category.BIO.getType() + "-" + mandatoryAuthPolicy.getAuthSubType()));
				}
			}
		} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(KYC)
				&& !Optional.ofNullable(requestBody.get("id"))
						.filter(id -> id.equals(env.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_API_ID + KYC)))
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

	/**
	 * Gets the auth part.
	 *
	 * @param requestWrapper the request wrapper
	 * @return the auth part
	 */
	protected Map<String, String> getAuthPart(ResettableStreamHttpServletRequest requestWrapper) {
		Map<String, String> params = new HashMap<>();
		String url = requestWrapper.getRequestURL().toString();
		String contextPath = requestWrapper.getContextPath();
		if ((Objects.nonNull(url) && !url.isEmpty()) && (Objects.nonNull(contextPath) && !contextPath.isEmpty())) {
			String[] splitedUrlByContext = url.split(contextPath);
			String[] paramsArray = Stream.of(splitedUrlByContext[1].split("/")).filter(str -> !str.isEmpty())
					.toArray(size -> new String[size]);

			if (paramsArray.length >= 3) {
				params.put(MISPLICENSE_KEY, paramsArray[paramsArray.length - 3]);
				params.put(PARTNER_ID, paramsArray[paramsArray.length - 2]);
				params.put(API_KEY, paramsArray[paramsArray.length - 1]);
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
		return env.getProperty("mosip.ida.auth.signing-required", Boolean.class, true);
	}

	/**
	 * Checks if is signature verification required.
	 *
	 * @return true, if is signature verification required
	 */
	@Override
	protected boolean isSignatureVerificationRequired() {
		return env.getProperty("mosip.ida.auth.signature-verification-required", Boolean.class, true);
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

	/**
	 * Checks if is trust validation required.
	 *
	 * @return true, if is trust validation required
	 */
	@Override
	protected boolean isTrustValidationRequired() {
		return env.getProperty("mosip.ida.auth.trust-validation-required", Boolean.class, true);
	}

}