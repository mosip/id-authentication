package io.mosip.admin.securitypolicy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.securitypolicy.constant.SecurityPolicyConstant;
import io.mosip.admin.securitypolicy.dto.AuthFactorsDto;
import io.mosip.admin.securitypolicy.service.SecurityPolicyService;
import io.mosip.kernel.core.http.ResponseWrapper;

@RestController
@RequestMapping("/security")
public class SecurityPolicyController {

	@Autowired
	private SecurityPolicyService securityPolicyService;

	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@GetMapping("/authfactors/{userId}")
	public ResponseWrapper<AuthFactorsDto> getAuthenticationFactorForUser(@PathVariable("userId") String userId) {
		ResponseWrapper<AuthFactorsDto> responseWrapper=new ResponseWrapper<>();
		AuthFactorsDto securityPolicy = securityPolicyService.getSecurityPolicy(userId);
		responseWrapper.setResponse(securityPolicy);
		responseWrapper.setMetadata(SecurityPolicyConstant.AUTHFACTOR_SUCCESS_METADATA);
		responseWrapper.setId(SecurityPolicyConstant.AUTHFACTOR_ID);
		responseWrapper.setVersion(SecurityPolicyConstant.AUTHFACTOR_VERSION);
		return responseWrapper;
	}
}
