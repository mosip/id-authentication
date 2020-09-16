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
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DIGITAL_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.EKYC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.HASH;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.HASH_INPUT_PARAM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.KYC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISPLICENSE_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST_HMAC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST_SESSION_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TIMESTAMP;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.KYCAttributes;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.BytesUtil;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
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
	
	private static final String TRANSACTION_ID = "transactionId";
	protected PartnerService partnerService;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(filterConfig.getServletContext());
		
		// Internal auth is not depending on partner service
		try {
			partnerService = context.getBean(PartnerService.class);
		 }catch(NoSuchBeanDefinitionException ex) {
		  //
		 }
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.
	 * util.Map)
	 */
	@Override
	protected Map<String, Object> decipherRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			if (null != requestBody.get(REQUEST)) {
				requestBody.replace(REQUEST,
						decode((String) requestBody.get(REQUEST)));
				if (null == requestBody.get(REQUEST_HMAC)) {
					throwMissingInputParameter(REQUEST_HMAC);
				} else {
					requestBody.replace(REQUEST_HMAC, decode((String) requestBody.get(REQUEST_HMAC)));
					Object encryptedSessionkey = decode((String) requestBody.get(REQUEST_SESSION_KEY));
					String reqHMAC = keyManager
							.kernelDecryptAndDecode(
									CryptoUtil.encodeBase64(CryptoUtil.combineByteArray(
											(byte[]) requestBody.get(REQUEST_HMAC), (byte[]) encryptedSessionkey,
											env.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER))),
									fetchReferenceId());
					Map<String, Object> request = keyManager.requestData(requestBody, mapper, fetchReferenceId(), 
							requestData -> validateRequestHMAC(reqHMAC, requestData));

					//If biometrics is present validate and decipher it.
					if(request.get(BIOMETRICS) != null) {
						validateBioDataInRequest(request);
						decipherBioData(request);
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
	 * @param obj the obj
	 * @param index 
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> decipherBioData(Object obj, int index) throws IdAuthenticationAppException {
		
		Map<String, Object> map = (Map<String, Object>) obj;

		if(!getStringValue(map, DATA).isPresent()) {
			throwMissingInputParameter(String.format(BIO_DATA_INPUT_PARAM, index));
		}
		
		if(!getStringValue(map, SESSION_KEY).isPresent()) {
			throwMissingInputParameter(String.format(BIO_SESSIONKEY_INPUT_PARAM, index));
		}
		
		try {
			byte[] decodedData = Objects.nonNull(map.get(DATA)) ? CryptoUtil.decodeBase64(getPayloadFromJwsSingature((String) map.get(DATA))) : new byte[0];
			Map<String, Object> data = mapper.readValue(decodedData, Map.class);
			
			if(!getStringValue(data, BIO_VALUE).isPresent()) {
				throwMissingInputParameter(String.format(BIO_VALUE_INPUT_PARAM, index));
			}
			
			if(!getStringValue(data, TIMESTAMP).isPresent()) {
				throwMissingInputParameter(String.format(BIO_TIMESTAMP_INPUT_PARAM, index));
			}
			
			Object bioValue = data.get(BIO_VALUE);
			
			String jwsSignature = (String)data.get(DIGITAL_ID);

			if(StringUtils.isNotEmpty(jwsSignature) ) {
				verifyDigitalIdSignature(jwsSignature);
				data.replace(DIGITAL_ID, decipherDigitalId(jwsSignature));
			}
			
			Object sessionKey = Objects.nonNull(map.get(SESSION_KEY)) ? map.get(SESSION_KEY) : null;
			String timestamp = String.valueOf(data.get(TIMESTAMP));
			String transactionId = String.valueOf(data.get(TRANSACTION_ID));
			byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
			byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(IdAuthConfigKeyConstants.IDA_SALT_LASTBYTES_NUM, Integer.class, DEFAULT_SALT_LAST_BYTES_NUM));
			String salt = CryptoUtil.encodeBase64(saltLastBytes);
			byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(IdAuthConfigKeyConstants.IDA_AAD_LASTBYTES_NUM, Integer.class, DEFAULT_AAD_LAST_BYTES_NUM));
			String aad = CryptoUtil.encodeBase64(aadLastBytes);
			String combinedData = combineDataForDecryption(String.valueOf(bioValue), String.valueOf(sessionKey));
			String decryptedData = keyManager.kernelDecrypt(combinedData, getBioRefId(), aad, salt);
			data.replace(BIO_VALUE, decryptedData);
			map.replace(DATA, data);
			return map;
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	protected String getBioRefId() {
		return env.getProperty(IdAuthConfigKeyConstants.PARTNER_BIO_REFERENCE_ID);
	}

	/**
	 * This method validates the digitalID signature.
	 * @param data
	 * @return
	 * @throws IdAuthenticationAppException
	 */
	private void verifyDigitalIdSignature(String jwsSignature) throws IdAuthenticationAppException{
		if(!super.verifySignature(jwsSignature)){
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}
	
	/**
	 * This method deciphers the digitalId from jws.
	 * @param data
	 * @return
	 * @throws IdAuthenticationAppException
	 */
	protected DigitalId decipherDigitalId(String jwsSignature) throws IdAuthenticationAppException {		
		try {
			return mapper.readValue(CryptoUtil.decodeBase64(getPayloadFromJwsSingature(jwsSignature)),DigitalId.class);
		}catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	/**
	 * Combine data for decryption.
	 *
	 * @param bioValue the bio value
	 * @param sessionKey the session key
	 * @return the string
	 */
	private String combineDataForDecryption(String bioValue, String sessionKey) {
		byte[] combineByteArray = CryptoUtil.combineByteArray(
				CryptoUtil.decodeBase64(bioValue),
				CryptoUtil.decodeBase64(sessionKey),
				env.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER));
		return CryptoUtil.encodeBase64(
				combineByteArray);
	}

	/**
	 * Method to get the reference id.
	 *
	 * @return the string
	 */
	protected String fetchReferenceId() {
		return env.getProperty(IdAuthConfigKeyConstants.PARTNER_REFERENCE_ID);
	}

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
			PartnerPolicyResponseDTO partnerServiceResponse =  getPartnerPolicyInfo(partnerId,partnerApiKey,licenseKey);
			checkAllowedAuthTypeBasedOnPolicy(partnerServiceResponse.getPolicy(), requestBody);
		}
	}

	private PartnerPolicyResponseDTO getPartnerPolicyInfo(String partnerId, String partnerApiKey, String licenseKey) throws IdAuthenticationAppException {
		try {
			return partnerService.validateAndGetPolicy(partnerId, partnerApiKey, licenseKey);
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);		
		}		
	}

	/**
	 * Validate bio data in request.
	 *
	 * @param requestBody the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private void validateBioDataInRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		List<Map<String, Object>> biometricsList = Optional.ofNullable(requestBody.get(BIOMETRICS))
				.filter(obj -> obj instanceof List).map(obj -> (List<Map<String, Object>>) obj)
				.orElse(Collections.emptyList());

		validateBioData(biometricsList);
	}

	/**
	 * Validate bio data.
	 *
	 * @param biometricsList the biometrics list
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void validateBioData(List<Map<String, Object>> biometricsList) throws IdAuthenticationAppException {
		try {
			String previousHash =  digest(getHash(""));
			
			for (int i = 0; i < biometricsList.size(); i++) {
				Map<String, Object> biometricData = biometricsList.get(i);
				Optional<String> dataOpt = getStringValue(biometricData, DATA);
				
				if(!dataOpt.isPresent()) {
					throwMissingInputParameter(String.format(BIO_DATA_INPUT_PARAM, i));
				}
				
				Optional<String> hashOpt = getStringValue(biometricData, HASH);
				
				if(!hashOpt.isPresent()) {
					throwMissingInputParameter(String.format(HASH_INPUT_PARAM, i));
				}
				
				String dataFieldValue = dataOpt.get();
				String data = extractBioData(dataFieldValue);
				
				previousHash = validateHash(data, hashOpt.get(), previousHash);
			}
		} catch (UnsupportedEncodingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	protected String extractBioData(String dataFieldValue) throws IdAuthenticationAppException {
		verifyJwsData(dataFieldValue);
		return getPayloadFromJwsSingature(dataFieldValue);
	}

	private String digest(byte[] hash) {
		return HMACUtils.digestAsPlainText(hash);
	}

	/**
	 * Gets the hash.
	 *
	 * @param string the string
	 * @return the hash
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private byte[] getHash(String string) throws UnsupportedEncodingException {
		return getHash(string.getBytes(UTF_8));
	}
	
	/**
	 * Gets the hash.
	 *
	 * @param bytes the bytes
	 * @return the hash
	 */
	private byte[] getHash(byte[] bytes) {
		return HMACUtils.generateHash(bytes);
	}

	/**
	 * Validate hash.
	 *
	 * @param biometricData the biometric data
	 * @param previousHash the previous hash
	 * @return the byte[]
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws UnsupportedEncodingException 
	 */
	private String validateHash(String data, String inputHashDigest, String previousHash) throws IdAuthenticationAppException, UnsupportedEncodingException {
	
		
		String currentHash =digest(getHash(CryptoUtil.decodeBase64(data)));
		String concatenatedHash = previousHash + currentHash;
		byte[] finalHash = getHash(concatenatedHash);
		String finalHashDigest = digest(finalHash);
		
		if(!inputHashDigest.equals(finalHashDigest)) {
			throwError(IdAuthenticationErrorConstants.INVALID_HASH);
		}
		
		return finalHashDigest;
		
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
	 * @param args the args
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void throwError(IdAuthenticationErrorConstants errorConst, String... args) throws IdAuthenticationAppException {
		if(args != null && args.length > 0 ) {
			throw new IdAuthenticationAppException(errorConst.getErrorCode(), 
					String.format(errorConst.getErrorMessage(), 
							(Object[])args));
		} else {
			throw new IdAuthenticationAppException(errorConst.getErrorCode(), errorConst.getErrorMessage());
		}
	}
	
	/**
	 * Gets the string value.
	 *
	 * @param map the biometric data
	 * @param fieldName the field name
	 * @return the string value
	 */
	private Optional<String> getStringValue(Map<String, Object> map, String fieldName) {
		return Optional.ofNullable(map.get(fieldName))
				.filter(obj -> obj instanceof String)
				.map(obj -> (String) obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#validateSignature(java.
	 * lang.String, byte[])
	 */
	@Override
	protected boolean validateRequestSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}
	
	/**
	 * Check allowed auth type based on policy.
	 *
	 * @param policyId
	 *            the policy id
	 * @param requestBody
	 *            the request body
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	protected void checkAllowedAuthTypeBasedOnPolicy(PolicyDTO policies, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {			
			if(policies != null) {		
				List<AuthPolicy> authPolicies = policies.getPolicies().getAuthPolicies();
				List<KYCAttributes> allowedKycAttributes = policies.getPolicies().getAllowedKycAttributes();
				List<String> allowedTypeList = allowedKycAttributes.stream().filter(KYCAttributes::isRequired)
						.map(KYCAttributes::getAttributeName).collect(Collectors.toList());
				if (allowedTypeList == null) {
					allowedTypeList = Collections.emptyList();
				}
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
	 * @param requestBody
	 *            the request body
	 * @param authPolicies
	 *            the auth policies
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
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
	 * @param requestBody            the request body
	 * @param authPolicies            the auth policies
	 * @throws IdAuthenticationAppException             the id authentication app exception
	 * @throws IOException             Signals that an I/O exception has occurred.
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

		OptionalInt noBioTypeIndex = IntStream.range(0, listBioInfo.size())
										.filter(i -> {
											BioIdentityInfoDTO bioIdInfoDto = listBioInfo.get(i);
											return Objects.nonNull(bioIdInfoDto.getData())
													&& StringUtils.isEmpty(bioIdInfoDto.getData().getBioType());
										}).findFirst();
		if (noBioTypeIndex.isPresent()) {
			throwMissingInputParameter(String.format(BIO_TYPE_INPUT_PARAM, noBioTypeIndex.getAsInt()));
		}
		
		OptionalInt nodeviceTypeIndex = IntStream.range(0, listBioInfo.size())
										.filter(i -> {
											BioIdentityInfoDTO bioIdInfoDto = listBioInfo.get(i);
											return Objects.nonNull(bioIdInfoDto.getData())
													&& StringUtils.isEmpty(bioIdInfoDto.getData().getDigitalId().getType());
										}).findFirst();
			
		if (nodeviceTypeIndex.isPresent()) {
					throwMissingInputParameter(String.format(BIO_DIGITALID_INPUT_PARAM_TYPE, nodeviceTypeIndex.getAsInt()));
		}

		List<String> bioTypeList = listBioInfo.stream()
									.map(s -> s.getData().getBioType())
									.collect(Collectors.toList());
		
		
		List<String> deviceTypeList = listBioInfo.stream()
				.map(s -> s.getData().getDigitalId().getType())
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
	 * @param authPolicies
	 *            the auth policies
	 * @param bioTypeList
	 *            the bio type list
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	private void checkAllowedAuthTypeForBio(List<AuthPolicy> authPolicies, List<String> bioTypeList, List<String> deviceTypeList)
			throws IdAuthenticationAppException {
		String bioAuthType;
		for (String bioType : bioTypeList) {
			bioAuthType = bioType;
			if (bioType.equalsIgnoreCase(BioAuthType.FGR_IMG.getType()) || 
					(FMR_ENABLED_TEST.test(env) && bioType.equalsIgnoreCase(BioAuthType.FGR_MIN.getType()))) {
				bioType = SingleType.FINGER.value();
			} else if (bioType.equalsIgnoreCase(BioAuthType.FACE_IMG.getType())) {
				bioType = SingleType.FACE.value();
			} else if (bioType.equalsIgnoreCase(BioAuthType.IRIS_IMG.getType())) {
				bioType = SingleType.IRIS.value();
			}

			if(!isDeviceTypeBioTypeSame(bioType,deviceTypeList)) {
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
	 * 
	 * @param bioType
	 * @param subTypeList
	 * @return
	 */
	private boolean isDeviceTypeBioTypeSame(String bioType, List<String> deviceTypeList) {		
		return deviceTypeList.stream().anyMatch(subType -> subType.equalsIgnoreCase(bioType));
	}
	
	/**
	 * Check mandatory auth type based on policy.
	 *
	 * @param requestBody
	 *            the request body
	 * @param mandatoryAuthPolicies
	 *            the mandatory auth policies
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
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
	 * @param requestBody
	 *            the request body
	 * @param authType
	 *            the auth type
	 * @param bioTypeList
	 *            the bio type list
	 * @param mandatoryAuthPolicy
	 *            the mandatory auth policy
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
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
						.filter(id -> id.equals(env.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_API_IDS + EKYC)))
						.isPresent()) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(), KYC));
		}
	}

	/**
	 * Checks if is allowed auth type.
	 *
	 * @param authType
	 *            the auth type
	 * @param policies
	 *            the policies
	 * @return true, if is allowed auth type
	 */
	protected boolean isAllowedAuthType(String authType, List<AuthPolicy> policies) {
		return isAllowedAuthType(authType, null, policies);
	}

	/**
	 * Checks if is allowed auth type.
	 *
	 * @param authType
	 *            the auth type
	 * @param subAuthType
	 *            the sub auth type
	 * @param policies
	 *            the policies
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
	 * @param requestWrapper
	 *            the request wrapper
	 * @return the auth part
	 */
	protected Map<String, String> getAuthPart(ResettableStreamHttpServletRequest requestWrapper) {
		Map<String, String> params = new HashMap<>();
		if (requestWrapper instanceof HttpServletRequestWrapper) {
			String url = requestWrapper.getRequestURL().toString();
			String contextPath = requestWrapper.getContextPath();
			if ((Objects.nonNull(url) && !url.isEmpty()) && (Objects.nonNull(contextPath) && !contextPath.isEmpty())) {
				String[] splitedUrlByContext = url.split(contextPath);
				String[] paramsArray = Stream.of(splitedUrlByContext[1].split("/")).filter(str -> !str.isEmpty())
						.toArray(size -> new String[size]);

				if (paramsArray.length >= 3) {
					params.put(MISPLICENSE_KEY, paramsArray[paramsArray.length - 3]);
					params.put(PARTNER_ID, paramsArray[paramsArray.length - 2]);
					params.put(API_KEY,paramsArray[paramsArray.length - 1]);
				}
			}
		}
		return params;
	}

}