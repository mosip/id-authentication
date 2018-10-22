package io.mosip.authentication.service.impl.indauth.service.demo;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;

@FunctionalInterface
public interface AuthTypeTester {
	boolean testAuthType(AuthRequestDTO authRequestDTO);
}
