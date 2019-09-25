package io.mosip.authentication.common.service.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.policy.dto.AuthPolicy;
import io.mosip.authentication.common.service.policy.dto.KYCAttributes;
import io.mosip.authentication.common.service.policy.dto.Policies;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class IdAuthFilter - the implementation for deciphering and validation of
 * the authenticating partner done for request as AUTH and KYC
 *
 * @author Manoj SP
 * @author Sanjay Murali
 */
@Component
public class IdAuthFilter extends BaseAuthFilter {

	private static final int DEFAULT_AAD_LAST_BYTES_NUM = 16;

	private static final int DEFAULT_SALT_LAST_BYTES_NUM = 12;

	private static final String TIMESTAMP = "timestamp";

	private static final String BIO_VALUE = "bioValue";

	private static final String REFID_IDA_FIR = "IDA-FIR";

	private static final String DATA = "data";

	private static final String SESSION_KEY = "sessionKey";

	/** The Constant EKYC. */
	private static final String EKYC = "ekyc";

	/** The Constant BIO_TYPE. */
	private static final String BIO_TYPE = "bioType";

	/** The Constant UTF_8. */
	private static final String UTF_8 = "UTF-8";

	/** The Constant REQUEST_HMAC. */
	private static final String REQUEST_HMAC = "requestHMAC";

	/** The Constant MISPLICENSE_KEY. */
	private static final String MISPLICENSE_KEY = "misplicenseKey";

	/** The Constant PARTNER_ID. */
	private static final String PARTNER_ID = "partnerId";

	/** The Constant MISP_ID. */
	private static final String MISP_ID = "mispId";

	/** The Constant POLICY_ID. */
	private static final String POLICY_ID = "policyId";

	/** The Constant ACTIVE_STATUS. */
	private static final String ACTIVE_STATUS = "active";

	/** The Constant EXPIRY_DT. */
	private static final String EXPIRY_DT = "expiryDt";

	/** The Constant KYC. */
	private static final String KYC = null;

	/** The Constant SESSION_KEY. */
	private static final String REQUEST_SESSION_KEY = "requestSessionKey";

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

			if (null != requestBody.get(IdAuthCommonConstants.REQUEST)) {
				requestBody.replace(IdAuthCommonConstants.REQUEST,
						decode((String) requestBody.get(IdAuthCommonConstants.REQUEST)));
				Map<String, Object> request = keyManager.requestData(requestBody, mapper, fetchReferenceId());
				if (null != requestBody.get(REQUEST_HMAC)) {
					requestBody.replace(REQUEST_HMAC, decode((String) requestBody.get(REQUEST_HMAC)));
					Object encryptedSessionkey = decode((String) requestBody.get(REQUEST_SESSION_KEY));
					String reqHMAC = keyManager
							.kernelDecryptAndDecode(
									CryptoUtil.encodeBase64(CryptoUtil.combineByteArray(
											(byte[]) requestBody.get(REQUEST_HMAC), (byte[]) encryptedSessionkey,
											env.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER))),
									fetchReferenceId());
					validateRequestHMAC(reqHMAC, mapper.writeValueAsString(request));

				}
				decipherBioData(request);
				requestBody.replace(IdAuthCommonConstants.REQUEST, request);
			}
			return requestBody;
		} catch (ClassCastException | JsonProcessingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void decipherBioData(Map<String, Object> request) throws IdAuthenticationAppException {
		Object biometrics = request.get(IdAuthCommonConstants.BIOMETRICS);
		if (Objects.nonNull(biometrics) && biometrics instanceof List) {
			List<Object> bioIdentity = (List<Object>) biometrics;
			List<Object> bioIdentityInfo = new ArrayList<>();
			for (Object obj : bioIdentity) {
				if (obj instanceof Map) {
					bioIdentityInfo.add(decipherBioData(obj));
				}
			}
			request.replace(IdAuthCommonConstants.BIOMETRICS, bioIdentityInfo);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> decipherBioData(Object obj) throws IdAuthenticationAppException {
		try {
			Map<String, Object> map = (Map<String, Object>) obj;
			Map<String, Object> data = mapper
					.readValue(Objects.nonNull(map.get(DATA)) ? CryptoUtil.decodeBase64((String) map.get(DATA)) : null,
							Map.class);
			Object bioValue = data.get(BIO_VALUE);
			Object sessionKey = Objects.nonNull(map.get(SESSION_KEY)) ? map.get(SESSION_KEY) : null;
			String timestamp = String.valueOf(data.get(TIMESTAMP));
			byte[] saltLastBytes = getLastBytes(timestamp, env.getProperty(IdAuthConfigKeyConstants.IDA_SALT_LASTBYTES_NUM, Integer.class, DEFAULT_SALT_LAST_BYTES_NUM));
			String salt = CryptoUtil.encodeBase64(saltLastBytes);
			byte[] aadLastBytes = getLastBytes(timestamp, env.getProperty(IdAuthConfigKeyConstants.IDA_AAD_LASTBYTES_NUM, Integer.class, DEFAULT_AAD_LAST_BYTES_NUM));
			String aad = CryptoUtil.encodeBase64(aadLastBytes);
			String combinedData = combineDataForDecryption(String.valueOf(bioValue), String.valueOf(sessionKey));
			String decryptedData = keyManager.kernelDecrypt(combinedData, env.getProperty(IdAuthConfigKeyConstants.CRYPTO_FIR_REF_ID, REFID_IDA_FIR), aad, salt);
			data.replace(BIO_VALUE, decryptedData);
			map.replace(DATA, data);
			return map;
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	private String combineDataForDecryption(String bioValue, String sessionKey) {
		byte[] combineByteArray = CryptoUtil.combineByteArray(
				CryptoUtil.decodeBase64(bioValue),
				CryptoUtil.decodeBase64(sessionKey),
				env.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER));
		return CryptoUtil.encodeBase64(
				combineByteArray);
	}

	private byte[] getLastBytes(String timestamp, int lastBytesNum) {
		assert(timestamp.length() >= lastBytesNum);
		return timestamp.substring(timestamp.length() - lastBytesNum).getBytes();
	}

	/**
	 * Method to get the reference id
	 *
	 * @return the string
	 */
	protected String fetchReferenceId() {
		return env.getProperty(IdAuthConfigKeyConstants.CRYPTO_PARTNER_ID);
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

		if (partnerId != null && licenseKey != null) {
			String mispId = licenseKeyMISPMapping(licenseKey);
			validPartnerId(partnerId);
			String policyId = validMISPPartnerMapping(partnerId, mispId);
			checkAllowedAuthTypeBasedOnPolicy(policyId, requestBody);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#validateSignature(java.
	 * lang.String, byte[])
	 */
	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

	/**
	 * License key MISP mapping is associated with this method.It checks for the
	 * license key expiry and staus.
	 *
	 * @param licenseKey
	 *            the license key
	 * @return the string
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private String licenseKeyMISPMapping(String licenseKey) throws IdAuthenticationAppException {
		String mispId;
		String licensekeyMappingJson = env.getProperty(IdAuthConfigKeyConstants.LICENSE_KEY + licenseKey);
		if (Objects.nonNull(licensekeyMappingJson)) {
			Map<String, Object> licenseKeyMap;
			try {
				licenseKeyMap = mapper.readValue(licensekeyMappingJson.getBytes(UTF_8), HashMap.class);
				mispId = String.valueOf(licenseKeyMap.get(MISP_ID));
			} catch (IOException e) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
			String lkExpiryDt = String.valueOf(licenseKeyMap.get(EXPIRY_DT));
			if (DateUtils.convertUTCToLocalDateTime(lkExpiryDt).isBefore(DateUtils.getUTCCurrentDateTime())) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED);
			}
			String lkStatus = String.valueOf(licenseKeyMap.get(IdAuthCommonConstants.STATUS));
			if (!lkStatus.equalsIgnoreCase(ACTIVE_STATUS)) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.LICENSEKEY_SUSPENDED);
			}
		} else {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_LICENSEKEY);
		}
		return mispId;
	}

	/**
	 * this method checks whether partner id is valid.
	 *
	 * @param partnerId
	 *            the partner id
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private void validPartnerId(String partnerId) throws IdAuthenticationAppException {
		String partnerIdJson = env.getProperty(IdAuthConfigKeyConstants.PARTNER_KEY + partnerId);
		if (null == partnerIdJson) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED);
		} else {
			Map<String, String> partnerIdMap;
			try {
				partnerIdMap = mapper.readValue(partnerIdJson.getBytes(UTF_8), Map.class);
			} catch (IOException e) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
			String policyId = partnerIdMap.get(POLICY_ID);
			if (null == policyId || policyId.equalsIgnoreCase("")) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_POLICY_NOTMAPPED);
			}
			String partnerStatus = partnerIdMap.get(IdAuthCommonConstants.STATUS);
			if (partnerStatus != null && !partnerStatus.equalsIgnoreCase(ACTIVE_STATUS)) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_DEACTIVATED);
			}
		}
	}

	/**
	 * Validates MISP partner mapping,if its valid it returns the policyId.
	 *
	 * @param partnerId
	 *            the partner id
	 * @param mispId
	 *            the misp id
	 * @return the string
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private String validMISPPartnerMapping(String partnerId, String mispId) throws IdAuthenticationAppException {
		Map<String, String> partnerIdMap = null;
		String policyId = null;
		Boolean mispPartnerMappingJson = env
				.getProperty(IdAuthConfigKeyConstants.MISP_PARTNER_MAPPING + mispId + "." + partnerId, boolean.class);
		if (null == mispPartnerMappingJson || !mispPartnerMappingJson) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOT_MAPPED);
		}
		String partnerIdJson = env.getProperty(IdAuthConfigKeyConstants.PARTNER_KEY + partnerId);
		try {
			partnerIdMap = mapper.readValue(partnerIdJson.getBytes(UTF_8), Map.class);
			policyId = partnerIdMap.get(POLICY_ID);
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		return policyId;
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
	protected void checkAllowedAuthTypeBasedOnPolicy(String policyId, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		try {
			String policyJson = getPolicy(policyId);
			Policies policies = mapper.readValue(policyJson.getBytes(UTF_8), Policies.class);
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
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
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
	 * @param requestBody
	 *            the request body
	 * @param authPolicies
	 *            the auth policies
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws JsonProcessingException
	 *             the json processing exception
	 */
	@SuppressWarnings("unchecked")
	private void checkAllowedAuthTypeForBio(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException, IOException {

		Object value = Optional.ofNullable(requestBody.get(IdAuthCommonConstants.REQUEST))
				.filter(obj -> obj instanceof Map).map(obj -> ((Map<String, Object>) obj).get("biometrics"))
				.filter(obj -> obj instanceof List).orElse(Collections.emptyList());
		List<BioIdentityInfoDTO> listBioInfo = mapper.readValue(mapper.writeValueAsBytes(value),
				new TypeReference<List<BioIdentityInfoDTO>>() {
				});

		boolean noBioType = listBioInfo.stream()
				.anyMatch(s -> Objects.nonNull(s.getData()) && StringUtils.isEmpty(s.getData().getBioType()));
		if (noBioType) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), BIO_TYPE));
		}

		List<String> bioTypeList = listBioInfo.stream()
				.filter(s -> Objects.nonNull(s.getData()) && !StringUtils.isEmpty(s.getData().getBioType()))
				.map(s -> s.getData().getBioType()).collect(Collectors.toList());
		if (bioTypeList.isEmpty()) {
			if (!isAllowedAuthType(MatchType.Category.BIO.getType(), authPolicies)) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), "bio"));
			}
		} else {
			checkAllowedAuthTypeForBio(authPolicies, bioTypeList);
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
	private void checkAllowedAuthTypeForBio(List<AuthPolicy> authPolicies, List<String> bioTypeList)
			throws IdAuthenticationAppException {
		String bioAuthType;
		for (String bioType : bioTypeList) {
			bioAuthType = bioType;
			if (bioType.equalsIgnoreCase(BioAuthType.FGR_IMG.getType())
					|| bioType.equalsIgnoreCase(BioAuthType.FGR_MIN.getType())) {
				bioType = SingleType.FINGER.value();
			} else if (bioType.equalsIgnoreCase(BioAuthType.FACE_IMG.getType())) {
				bioType = SingleType.FACE.value();
			} else if (bioType.equalsIgnoreCase(BioAuthType.IRIS_IMG.getType())) {
				bioType = SingleType.IRIS.value();
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
					.filter(obj -> obj instanceof Map).map(obj -> ((Map<String, Object>) obj).get("biometrics"))
					.filter(obj -> obj instanceof List).orElse(Collections.emptyList());
			List<BioIdentityInfoDTO> listBioInfo = mapper.readValue(mapper.writeValueAsBytes(value),
					new TypeReference<List<BioIdentityInfoDTO>>() {
					});
			List<String> bioTypeList = listBioInfo.stream().map(s -> s.getData().getBioType())
					.collect(Collectors.toList());
			if (bioTypeList.contains("FMR") || bioTypeList.contains("FIR")) {
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
	 * Gets the policy.
	 *
	 * @param policyId
	 *            the policy id
	 * @return the policy
	 */
	private String getPolicy(String policyId) {
		return env.getProperty(IdAuthConfigKeyConstants.POLICY + policyId);
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

				if (paramsArray.length >= 2) {
					params.put(PARTNER_ID, paramsArray[paramsArray.length - 2]);
					params.put(MISPLICENSE_KEY, paramsArray[paramsArray.length - 1]);
				}
			}
		}
		return params;
	}

}
