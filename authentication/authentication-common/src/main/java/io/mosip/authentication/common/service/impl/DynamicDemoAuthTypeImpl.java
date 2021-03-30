package io.mosip.authentication.common.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

/**
 * The Class DynamicDemoAuthTypeImpl.
 * 
 * @author Loganathan Sekar
 */
public class DynamicDemoAuthTypeImpl extends AuthTypeImpl {

	/**
	 * Instantiates a new dynamic demo auth type impl.
	 *
	 * @param type the type
	 * @param associatedMatchTypes the associated match types
	 */
	public DynamicDemoAuthTypeImpl(String type, Set<MatchType> associatedMatchTypes) {
		super(type, associatedMatchTypes, type);
	}
	
	/**
	 * Gets the display name.
	 *
	 * @param authReq the auth req
	 * @param idInfoFetcher the id info fetcher
	 * @return the display name
	 */
	@Override
	public String getDisplayName(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return getAvailableDynamicAttributeNames(authReq, idInfoFetcher)
					.stream()
					.collect(Collectors.joining(","));
	}
	
	/**
	 * Checks if is auth type enabled.
	 *
	 * @param authReq the auth req
	 * @param idInfoFetcher the id info fetcher
	 * @return true, if is auth type enabled
	 */
	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return !getAvailableDynamicAttributeNames(authReq, idInfoFetcher).isEmpty();
	}

	/**
	 * Gets the available dynamic attribute names.
	 *
	 * @param authReq the auth req
	 * @param idInfoFetcher the id info fetcher
	 * @return the available dynamic attribute names
	 */
	private Set<String> getAvailableDynamicAttributeNames(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return idInfoFetcher.getAvailableDynamicAttributesNames(authReq.getRequest());
	}
	
	

}
