package io.mosip.authentication.common.service.impl;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

/**
 * The Class AuthTypeImpl - used to instantiate respective
 * Auth type to set the type, associate match types, display name
 * and to get which auth type is enabled
 * 
 * @author Sanjay Murali
 */
public class AuthTypeImpl implements AuthType {
	
	/** The display name. */
	private String displayName;
	
	/** The type. */
	private String type;
	
	/** The associated match types. */
	private Set<MatchType> associatedMatchTypes;
	
	/** The auth type predicate. */
	private Predicate<? super AuthTypeDTO> authTypePredicate;
	
	/**
	 * Instantiates a new auth type impl.
	 *
	 * @param type the type
	 * @param associatedMatchTypes the associated match types
	 * @param authTypePredicate the auth type predicate
	 * @param displayName the display name
	 */
	public AuthTypeImpl(String type, Set<MatchType> associatedMatchTypes,
			Predicate<? super AuthTypeDTO> authTypePredicate, String displayName) {
		this(type, associatedMatchTypes, displayName);
		this.authTypePredicate = authTypePredicate;
	}
	
	/**
	 * Instantiates a new auth type impl.
	 *
	 * @param type the type
	 * @param associatedMatchTypes the associated match types
	 * @param displayName the display name
	 */
	public AuthTypeImpl(String type, Set<MatchType> associatedMatchTypes, String displayName) {
		this.type = type;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
		this.displayName = displayName;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#isAuthTypeEnabled(io.mosip.authentication.core.dto.indauth.AuthRequestDTO, io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher)
	 */
	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
		return false;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getAssociatedMatchTypes()
	 */
	@Override
	public Set<MatchType> getAssociatedMatchTypes() {
		return associatedMatchTypes;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#isAuthTypeInfoAvailable(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	@Override
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getAuthTypePredicate()
	 */
	@Override
	public Predicate<? super AuthTypeDTO> getAuthTypePredicate() {
		return authTypePredicate;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getAuthTypeImpl()
	 */
	@Override
	public AuthType getAuthTypeImpl() {
		return this;
	}

}
