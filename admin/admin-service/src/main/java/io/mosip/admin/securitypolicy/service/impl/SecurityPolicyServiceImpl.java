package io.mosip.admin.securitypolicy.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.securitypolicy.config.PolicyProperties;
import io.mosip.admin.securitypolicy.constant.SecurityPolicyErrorConstant;
import io.mosip.admin.securitypolicy.dto.AuthFactorsDto;
import io.mosip.admin.securitypolicy.dto.UserRoleDto;
import io.mosip.admin.securitypolicy.dto.UserRoleResponseDto;
import io.mosip.admin.securitypolicy.exception.SecurityPolicyException;
import io.mosip.admin.securitypolicy.service.SecurityPolicyService;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Security Policy service implementation.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 * 
 */

@Service
public class SecurityPolicyServiceImpl implements SecurityPolicyService {

	/**
	 * field of policy properties
	 */
	@Autowired
	private PolicyProperties policyProps;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * field of auth-service url for fetching user roles
	 */
	@Value("${mosip.admin.security.policy.userrole-auth-url}")
	private String userRoleAuthServiceUrl;

	/**
	 * field of application id
	 */
	@Value("${mosip.admin.app-id}")
	private String appId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.admin.securitypolicy.service.SecurityPolicyService#getSecurityPolicy
	 * (java.lang.String)
	 */
	@Override
	public AuthFactorsDto getSecurityPolicy(String username) {
		AuthFactorsDto securityPolicyDto = null;
		UserRoleDto userRole = null;
		Set<String> policies = null;
		Set<String> authTypes = null;
		UserRoleResponseDto responseWrapper = null;

		try {
			responseWrapper = restTemplate.getForObject(userRoleAuthServiceUrl, UserRoleResponseDto.class, appId,
					username);
		} catch (RestClientException e) {
			throw new SecurityPolicyException(SecurityPolicyErrorConstant.ERROR_FETCHING_USER_ROLE.errorCode(),
					SecurityPolicyErrorConstant.ERROR_FETCHING_USER_ROLE.errorMessage(), e);
		}
		if (responseWrapper.getErrors() != null && !responseWrapper.getErrors().isEmpty()) {
			ServiceError error = responseWrapper.getErrors().get(0);
			throw new SecurityPolicyException(error.getErrorCode(), error.getMessage());
		}
		userRole = responseWrapper.getResponse();
		Set<String> roles = Arrays.stream(userRole.getRole().split(",")).map(String::toUpperCase)
				.collect(Collectors.toSet());
		Map<String, String> rolePolicy = policyProps.getRolePolicyMapping();
		Map<String, Set<String>> policyAuth = policyProps.getPolicyAuth();
		policies = roles.stream().map(rolePolicy::get).filter(Objects::nonNull).collect(Collectors.toSet());
		authTypes = new HashSet<>();
		if (policies != null && !policies.isEmpty()) {
			for (String policy : policies) {
				String[] pArray = policy.split(",");
				for (String p : pArray) {
					Set<String> auth = policyAuth.get(p);
					if (auth != null)
						authTypes.addAll(auth);
				}
			}
		} else {
			throw new SecurityPolicyException(SecurityPolicyErrorConstant.NO_POLICY_FOUND.errorCode(),
					SecurityPolicyErrorConstant.NO_POLICY_FOUND.errorMessage() + roles);
		}
		securityPolicyDto = new AuthFactorsDto();
		securityPolicyDto.setAuthTypes(authTypes);
		return securityPolicyDto;
	}

}
