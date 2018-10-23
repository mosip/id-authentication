package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;

@FunctionalInterface
public interface MatchThresholdInfoFetcher {
	Optional<Integer> getMatchThreshold(AuthRequestDTO authRequestDTO);
}
