package io.mosip.authentication.common.service.filter;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.policy.dto.AuthPolicy;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class OTPFilter.
 *
 * @author Manoj SP
 */
@Component
public class BaseInternalFilter extends IdAuthFilter {


	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.filter.IdAuthFilter#validateSignature(java.lang.String, byte[])
	 */
	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.filter.IdAuthFilter#checkAllowedAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
	 */
	@Override
	protected void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException {
		// Nothing to do
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

}
