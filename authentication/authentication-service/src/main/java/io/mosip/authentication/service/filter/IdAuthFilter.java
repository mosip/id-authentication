package io.mosip.authentication.service.filter;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.service.policy.AuthPolicy;
import io.mosip.authentication.service.policy.KYCAttributes;
import io.mosip.authentication.service.policy.Policies;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class IdAuthFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthFilter extends BaseAuthFilter {

	private static final String MISP_PARTNER_MAPPING = "misp.partner.mapping.";

	private static final String PARTNER_KEY = "partner.";

	/** The Constant LICENSE_KEY. */
	private static final String LICENSE_KEY = "licenseKey.";

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

	/** The Constant STATUS. */
	private static final String STATUS = "status";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant KYC. */
	private static final String KYC = null;

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
			requestBody.replace(REQUEST, decode((String) requestBody.get(REQUEST)));
			if (null != requestBody.get(REQUEST)) {
				Map<String, Object> request = keyManager.requestData(requestBody, mapper);
				requestBody.replace(REQUEST, request);

				validateRequestHMAC((String) requestBody.get("requestHMAC"), mapper.writeValueAsString(request));
			}
			return requestBody;
		} catch (ClassCastException | JsonProcessingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
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
		String mispId = licenseKeyMISPMapping(licenseKey);
		validPartnerId(partnerId);
		String policyId = validMISPPartnerMapping(partnerId, mispId);
		checkAllowedAuthTypeBasedOnPolicy(policyId, requestBody);
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
	 * @param licenseKey the license key
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private String licenseKeyMISPMapping(String licenseKey) throws IdAuthenticationAppException {
		String mispId = null;
		Map<String, Object> licenseKeyMap = null;
		String licensekeyMappingJson = env.getProperty(LICENSE_KEY + licenseKey);
		if (null != licensekeyMappingJson) {
			try {
				licenseKeyMap = mapper.readValue(licensekeyMappingJson.getBytes("UTF-8"), HashMap.class);
				mispId = (String) licenseKeyMap.get(MISP_ID);
			} catch (IOException e) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
			String lkExpiryDt = (String) licenseKeyMap.get(EXPIRY_DT);
			if (DateUtils.convertUTCToLocalDateTime(lkExpiryDt).isBefore(DateUtils.getUTCCurrentDateTime())) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED);
			}
			String lkStatus = (String) licenseKeyMap.get(STATUS);
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
	 * @param partnerId the partner id
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void validPartnerId(String partnerId) throws IdAuthenticationAppException {
		String partnerIdJson = env.getProperty(PARTNER_KEY + partnerId);
		Map<String, String> partnerIdMap = null;
		if (null == partnerIdJson) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED);
		} else {
			try {
				partnerIdMap = mapper.readValue(partnerIdJson.getBytes("UTF-8"), Map.class);
			} catch (IOException e) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
			String policyId = partnerIdMap.get(POLICY_ID);
			if (null == policyId || policyId.equalsIgnoreCase("")) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_POLICY_NOTMAPPED);
			}
			String partnerStatus = partnerIdMap.get(STATUS);
			if (!partnerStatus.equalsIgnoreCase(ACTIVE_STATUS)) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_DEACTIVATED);
			}
		}
	}

	/**
	 * Validates MISP partner mapping,if its valid it returns the policyId.
	 *
	 * @param partnerId the partner id
	 * @param mispId    the misp id
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private String validMISPPartnerMapping(String partnerId, String mispId) throws IdAuthenticationAppException {
		Map<String, String> partnerIdMap = null;
		String policyId = null;
		boolean mispPartnerMappingJson = env.getProperty(MISP_PARTNER_MAPPING + mispId + "." + partnerId,
				boolean.class);
		if (!mispPartnerMappingJson) {

			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOT_MAPPED);
		}
		String partnerIdJson = env.getProperty(PARTNER_KEY + partnerId);
		try {
			partnerIdMap = mapper.readValue(partnerIdJson.getBytes("UTF-8"), Map.class);
			policyId = partnerIdMap.get(POLICY_ID);
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		return policyId;
	}

	/**
	 * Check allowed auth type based on policy.
	 *
	 * @param policyId    the policy id
	 * @param requestBody the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void checkAllowedAuthTypeBasedOnPolicy(String policyId, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		try {
			String policyJson = getPolicy(policyId);
			Policies policies = null;
			policies = mapper.readValue(policyJson.getBytes("UTF-8"), Policies.class);
			//Get kyc attributes
			List<AuthPolicy> authPolicies = policies.getPolicies().getAuthPolicies();
			List<KYCAttributes> allowedKycAttributes = policies.getPolicies().getAllowedKycAttributes();
			List<String> allowedTypeList = allowedKycAttributes.stream().map(value -> value.getAttributeName())
					.collect(Collectors.toList());
			if (allowedTypeList == null) {
				allowedTypeList = Collections.emptyList();
			}
			requestBody.put("allowedKycAttributes", allowedTypeList);
			checkAllowedAuthTypeBasedOnPolicy(requestBody, authPolicies);
			List<AuthPolicy> mandatoryAuthPolicies = authPolicies.stream().filter(policy -> policy.isMandatory())
					.collect(Collectors.toList());
			checkMandatoryAuthTypeBasedOnPolicy(requestBody, mandatoryAuthPolicies);
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Check allowed auth type based on policy.
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
				if (!isAllowedAuthType(MatchType.Category.BIO.getType(), authPolicies)) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
									MatchType.Category.BIO.name()));
				} else {
					Object value = Optional.ofNullable(requestBody.get("request")).filter(obj -> obj instanceof Map)
							.map(obj -> ((Map<String, Object>) obj).get("biometrics"))
							.filter(obj -> obj instanceof List).orElse(Collections.emptyList());
					List<BioIdentityInfoDTO> listBioInfo = mapper.readValue(mapper.writeValueAsBytes(value),
							new TypeReference<List<BioIdentityInfoDTO>>() {
							});

					List<String> bioTypeList = listBioInfo.stream().map(s -> s.getData().getBioType())
							.collect(Collectors.toList());
					for (String bioType : bioTypeList) {
						if (bioType.equalsIgnoreCase("FIR") || bioType.equalsIgnoreCase("FMR")) {
							bioType = "FINGER";
						}
						if (!isAllowedAuthType(MatchType.Category.BIO.getType(), bioType, authPolicies)) {
							throw new IdAuthenticationAppException(
									IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
									String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(),
											MatchType.Category.BIO.name() + "-" + bioType));
						}
					}
				}
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
	 * Check mandatory auth type based on policy.
	 *
	 * @param requestBody           the request body
	 * @param mandatoryAuthPolicies the mandatory auth policies
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void checkMandatoryAuthTypeBasedOnPolicy(Map<String, Object> requestBody,
			List<AuthPolicy> mandatoryAuthPolicies) throws IdAuthenticationAppException {
		try {
			AuthTypeDTO authType = mapper.readValue(mapper.writeValueAsBytes(requestBody.get("requestedAuth")),
					AuthTypeDTO.class);
			Object value = Optional.ofNullable(requestBody.get("request")).filter(obj -> obj instanceof Map)
					.map(obj -> ((Map<String, Object>) obj).get("biometrics")).filter(obj -> obj instanceof List)
					.orElse(Collections.emptyList());
			List<BioIdentityInfoDTO> listBioInfo = mapper.readValue(mapper.writeValueAsBytes(value),
					new TypeReference<List<BioIdentityInfoDTO>>() {
					});
			List<String> bioTypeList = listBioInfo.stream().map(s -> s.getData().getBioType())
					.collect(Collectors.toList());
			if (bioTypeList.contains("FMR") || bioTypeList.contains("FIR")) {
				bioTypeList.add("FINGER");
			}
			for (AuthPolicy mandatoryAuthPolicy : mandatoryAuthPolicies) {
				if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.OTP.getType())
						&& !authType.isOtp()) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
									MatchType.Category.OTP.getType()));
				} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.DEMO.getType())
						&& !authType.isDemo()) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
									MatchType.Category.DEMO.getType()));
				} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.SPIN.getType())
						&& !authType.isPin()) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
									MatchType.Category.SPIN.getType()));
				} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(MatchType.Category.BIO.getType())) {
					if (!authType.isBio()) {
						throw new IdAuthenticationAppException(
								IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
								String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
										MatchType.Category.BIO.getType()));
					} else {
						if (!bioTypeList.contains(mandatoryAuthPolicy.getAuthSubType())) {
							throw new IdAuthenticationAppException(
									IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(),
									String.format(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(),
											MatchType.Category.BIO.getType() + "-"
													+ mandatoryAuthPolicy.getAuthSubType()));
						}
					}
				} else if (mandatoryAuthPolicy.getAuthType().equalsIgnoreCase(KYC)) {
					if (!Optional.ofNullable(requestBody.get("id"))
							.filter(id -> id.equals(env.getProperty("mosip.ida.api.ids.ekyc"))).isPresent()) {
						throw new IdAuthenticationAppException(
								IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(), String.format(
										IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorMessage(), KYC));
					}
				}
			}
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
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
	 * Gets the policy.
	 *
	 * @param policyId the policy id
	 * @return the policy
	 */
	private String getPolicy(String policyId) {
		return env.getProperty("policy." + policyId);
	}

	/**
	 * Gets the auth part.
	 *
	 * @param requestWrapper the request wrapper
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

				params.put(PARTNER_ID, paramsArray[2]);
				params.put(MISPLICENSE_KEY, paramsArray[3]);
			}
		}
		return params;
	}

}
