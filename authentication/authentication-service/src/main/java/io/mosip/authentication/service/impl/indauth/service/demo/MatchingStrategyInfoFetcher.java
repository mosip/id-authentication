package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;

@FunctionalInterface
public interface MatchingStrategyInfoFetcher {
	Optional<String> getMatchingStratogy(AuthRequestDTO authRequestDTO);
}
