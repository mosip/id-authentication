package io.mosip.authentication.common.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

public class DynamicDemoAuthTypeImpl extends AuthTypeImpl {

	public DynamicDemoAuthTypeImpl(String type, Set<MatchType> associatedMatchTypes) {
		super(type, associatedMatchTypes, type);
	}
	
	@Override
	public String getDisplayName(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return getAvailableDynamicAttributeNames(authReq, idInfoFetcher)
					.stream()
					.collect(Collectors.joining(","));
	}
	
	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return !getAvailableDynamicAttributeNames(authReq, idInfoFetcher).isEmpty();
	}

	private Set<String> getAvailableDynamicAttributeNames(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return idInfoFetcher.getAvailableDynamicAttributesNames(authReq.getRequest());
	}
	
	

}
