package io.mosip.admin.securitypolicy.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.mosip.admin.securitypolicy.constant.SecurityPolicyErrorConstant;
import io.mosip.admin.securitypolicy.exception.SecurityPolicyException;
import lombok.Getter;

/**
 * containing security policy configurations
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Configuration
@Getter
public class PolicyProperties {

	private String prefix = "mosip.admin.security.policy";

	@Value("#{'${mosip.admin.security.policy.auth-types}'.split(',')}")
	private List<String> authenticationTypes;

	@Value("#{'${mosip.admin.security.policy.policy-types}'.split(',')}")
	private List<String> policyTypes;

	@Value("#{${mosip.admin.security.policy.role-policy-mapping}}")
	private Map<String, String> rolePolicyMapping;

	private Map<String, Set<String>> policyAuth;
	private Map<String, Set<String>> authTypesMap;

	@Autowired
	private Environment env;

	@PostConstruct
	public void constructProperties() {
		fetchPolicyAuth();
		fetchAuthTypes();
		validateAuthTypes();
	}

	public void fetchPolicyAuth() {
		policyAuth = new HashMap<>();
		for (String type : policyTypes) {
			String propKey = prefix + "." + type;
			String value = env.getProperty(propKey);
			if (value != null) {
				Set<String> authTypes = Arrays.stream(value.split(",")).collect(Collectors.toSet());
				policyAuth.put(type, authTypes);
			}
		}

	}

	public void fetchAuthTypes() {
		authTypesMap = new HashMap<>();
		for (String type : authenticationTypes) {
			String propKey = prefix + "." + type;
			String value = env.getProperty(propKey);
			if (value != null) {
				Set<String> authTypes = Arrays.stream(value.split(",")).collect(Collectors.toSet());
				authTypesMap.put(type, authTypes);
			}
		}

	}

	private void validateAuthTypes() {
		for (Entry<String, Set<String>> policy : policyAuth.entrySet()) {
			Set<String> authTypes = policy.getValue();
			for (String auth : authTypes) {
				boolean flag = Boolean.FALSE;
				for (Entry<String, Set<String>> authKey : authTypesMap.entrySet()) {
					Set<String> set = authKey.getValue();
					if (set.contains(auth)) {
						flag = true;
					}
				}
				if (!flag) {
					throw new SecurityPolicyException(SecurityPolicyErrorConstant.NO_AUTH_TYPE_FOUND.errorCode(),
							SecurityPolicyErrorConstant.NO_AUTH_TYPE_FOUND.errorMessage() + auth);
				}

			}

		}
	}

}
