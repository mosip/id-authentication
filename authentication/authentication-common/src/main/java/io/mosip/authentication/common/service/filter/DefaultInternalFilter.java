package io.mosip.authentication.common.service.filter;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class OTPFilter.
 *
 * @author Manoj SP
 */
@Component
public class DefaultInternalFilter extends InternalAuthFilter {
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.filter.BaseAuthFilter#decipherAndValidateRequest(io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest, java.util.Map)
	 */
	protected void decipherAndValidateRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		//Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.common.service.filter.IdAuthFilter#
	 * checkAllowedAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
	 */
	@Override
	protected void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException {
		// Nothing to do
	}

	/**
	 * This method is used to construct the response for OTP by removing the null
	 * values
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.common.service.filter.IdAuthFilter#
	 * checkMandatoryAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
	 */
	@Override
	protected void checkMandatoryAuthTypeBasedOnPolicy(Map<String, Object> requestBody,
			List<AuthPolicy> mandatoryAuthPolicies) throws IdAuthenticationAppException {
		// Nothing to do
	}

	@Override
	protected String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute) {
		String id = null;
		String reqUrl = requestWrapper.getRequestURL().toString();
		if (reqUrl != null && !reqUrl.isEmpty()) {
			String[] path = reqUrl.split(IdAuthCommonConstants.INTERNAL_URL);
			if (path[1] != null && !path[1].isEmpty()) {
				String[] urlPath = path[1].split("/");
				String contextPath = urlPath[1];
				if (!StringUtils.isEmpty(contextPath)) {
					if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.OTP)) {
						id = attribute + IdAuthConfigKeyConstants.OTP_INTERNAL_ID_SUFFIX;
					} else if (contextPath.equalsIgnoreCase(IdAuthCommonConstants.AUTH_TRANSACTIONS)) {
						id = attribute + IdAuthConfigKeyConstants.AUTH_TRANSACTION;
					}
				}
			}
		}
		return id;
	}
	
	protected boolean needStoreAuthTransaction() {
		return false;
	}
	
	protected boolean needStoreAnonymousProfile() {
		return false;
	}


}
