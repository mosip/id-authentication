package io.mosip.authentication.otp.service.filter;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.filter.IdAuthFilter;
import io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;

/**
 * The Class OTPFilter.
 *
 * @author Manoj SP
 */
@Component
public class OTPFilter extends IdAuthFilter {
	
	/** The Constant AUTH. */
	private static final String OTP = "otp";

	/** The Constant OTP_REQUEST. */
	private static final String OTP_REQUEST = "otp-request";

	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.filter.IdAuthFilter#checkAllowedAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
	 */
	@Override
	protected void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException {
		if (!isAllowedAuthType(OTP_REQUEST, authPolicies)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.OTPREQUEST_NOT_ALLOWED);
		}
	}

	/**
	 * This method is used to construct the response
	 * for OTP by removing the null values
	 * 
	 * @param responseMap the response map
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> removeNullOrEmptyFieldsInResponse(Map<String, Object> responseMap) {
		return responseMap.entrySet().stream().filter(map -> Objects.nonNull(map.getValue()))
				.filter(entry -> !(entry.getValue() instanceof List) || !((List<?>) entry.getValue()).isEmpty())
				.map(entry -> {
					if ((entry.getValue() instanceof Map)) {
						Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
						Map<String, Object> changedMap = removeNullOrEmptyFieldsInResponse(innerMap);
						return new SimpleEntry<String, Object>(entry.getKey(), changedMap);
					}
					return entry;
				}).collect(Collectors.toMap(Entry<String, Object>::getKey, Entry<String, Object>::getValue,
						(map1, map2) -> map1, LinkedHashMap<String, Object>::new));
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.filter.IdAuthFilter#checkMandatoryAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
	 */
	@Override
	protected void checkMandatoryAuthTypeBasedOnPolicy(Map<String, Object> requestBody,
			List<AuthPolicy> mandatoryAuthPolicies) throws IdAuthenticationAppException {
		// Nothing to do
	}

	@Override
	protected boolean isSigningRequired() {
		return true;
	}

	@Override
	protected boolean isSignatureVerificationRequired() {
		return true;
	}

	//After integration with 1.1.5.1 version of keymanager, thumbprint is always mandated for decryption.
//	@Override
//	protected boolean isThumbprintValidationRequired() {
//		return env.getProperty("mosip.ida.otp.thumbprint-validation-required", Boolean.class, true);
//	}

	@Override
	protected boolean isTrustValidationRequired() {
		return true;
	}
	
	/**
	 * Fetch id.
	 *
	 * @param requestWrapper the request wrapper
	 * @param attribute the attribute
	 * @return the string
	 */
	@Override
	protected String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute) {
		return attribute + OTP;
	}
	
	protected boolean needStoreAuthTransaction() {
		return true;
	}
	
	protected boolean needStoreAnonymousProfile() {
		return false;
	}

	@Override
	protected boolean isMispPolicyValidationRequired() {
		return false;
	}

	@Override
	protected boolean isCertificateValidationRequired() {
		return true;
	}

	@Override
	protected boolean isAMRValidationRequired() {
		return false;
	}
}
