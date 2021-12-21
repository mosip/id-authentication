package io.mosip.authentication.common.service.impl.match;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.common.service.impl.DynamicDemoAuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

/**
 * The Enum DemoAuthType.
 *
 * @author Dinesh Karuppiah.T
 * @author Nagarjuna
 */
public enum DemoAuthType implements AuthType {

	// @formatter:off

	/** The address. */
	ADDRESS("address",
			AuthType.setOf(DemoMatchType.ADDR_LINE1, DemoMatchType.ADDR_LINE2, DemoMatchType.ADDR_LINE3,
					DemoMatchType.LOCATION1, DemoMatchType.LOCATION2, DemoMatchType.LOCATION3, DemoMatchType.PINCODE), "Address"),

	/** The pi pri. */
	PERSONAL_IDENTITY("personalIdentity",
			AuthType.setOf(DemoMatchType.NAME, DemoMatchType.DOB, DemoMatchType.DOBTYPE, DemoMatchType.AGE,
					DemoMatchType.EMAIL, DemoMatchType.PHONE, DemoMatchType.GENDER), "Personal Identity"),

	/** The full address. */
	FULL_ADDRESS("fullAddress", AuthType.setOf(DemoMatchType.ADDR), "Full Address"),
	
	/** The dynamic. */
	DYNAMIC("demographics", AuthType.setOf(DemoMatchType.DYNAMIC)){
		
		public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
			return getAuthTypeImpl().isAuthTypeEnabled(authReq, idInfoFetcher);
		}
		
	}
		
	;


	// @formatter:on
	
	/** The Constant ENGLISH. */
	private static final String ENGLISH = "english";
	
	/** The auth type impl. */
	private AuthTypeImpl authTypeImpl;

	/**
	 * Instantiates a new demo auth type.
	 *
	 * @param type                 the type
	 * @param associatedMatchTypes the associated match types
	 * @param authTypePredicate    the auth type predicate
	 * @param displayName          the display name
	 */
	private DemoAuthType(String type, Set<MatchType> associatedMatchTypes, String displayName) {
		authTypeImpl = new AuthTypeImpl(type, associatedMatchTypes, displayName);
	}
	
	/**
	 * Instantiates a new demo auth type.
	 *
	 * @param type the type
	 * @param associatedMatchTypes the associated match types
	 */
	private DemoAuthType(String type, Set<MatchType> associatedMatchTypes) {
		authTypeImpl = new DynamicDemoAuthTypeImpl(type, associatedMatchTypes);
	}

	/**
	 * Gets the match properties.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfoFetcher the id info fetcher
	 * @param language the language
	 * @return the match properties
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.AuthType#getMatchProperties(io
	 * .mosip.authentication.core.dto.indauth.AuthRequestDTO,
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher)
	 */
	@Override
	public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, 
			IdInfoFetcher idInfoFetcher,String language) {
		HashMap<String, Object> valuemap = new HashMap<>();
		Optional<String> languageNameOpt = idInfoFetcher.getLanguageName(language);
		valuemap.put("language", languageNameOpt.orElse(ENGLISH));
		valuemap.put("env", idInfoFetcher.getEnvironment());
		valuemap.put("titlesFetcher", idInfoFetcher.getTitleFetcher());
		valuemap.put("langCode", language);
		valuemap.put("demoNormalizer", idInfoFetcher.getDemoNormalizer());
		valuemap.put("demoMatcherUtil", idInfoFetcher.getDemoMatcherUtil());
		return valuemap;
	}

	/**
	 * Gets the auth type impl.
	 *
	 * @return the auth type impl
	 */
	@Override
	public AuthType getAuthTypeImpl() {
		return authTypeImpl;
	}
	
}
