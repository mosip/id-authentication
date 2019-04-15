package io.mosip.authentication.otp.service.filter;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.authentication.filter.IdAuthFilter;
import io.mosip.authentication.common.policy.AuthPolicy;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class OTPFilter.
 *
 * @author Manoj SP
 */
@Component
public class OTPFilter extends IdAuthFilter {

	private static final String OTP_REQUEST = "otp-request";

	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

	@Override
	protected void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException {
		if (!isAllowedAuthType(OTP_REQUEST, authPolicies)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), OTP_REQUEST));
		}
	}

	/**
	 * Construct response.
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

	@Override
	protected void checkMandatoryAuthTypeBasedOnPolicy(Map<String, Object> requestBody,
			List<AuthPolicy> mandatoryAuthPolicies) throws IdAuthenticationAppException {
		// Nothing to do
	}

}
