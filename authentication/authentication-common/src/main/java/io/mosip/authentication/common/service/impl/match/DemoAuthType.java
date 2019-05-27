package io.mosip.authentication.common.service.impl.match;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.spi.bioauth.util.BioMatcherUtil;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

/**
 * The Enum DemoAuthType.
 *
 * @author Dinesh Karuppiah.T
 */
public enum DemoAuthType implements AuthType {

	// @formatter:off

	ADDRESS("address",
			AuthType.setOf(DemoMatchType.ADDR_LINE1, DemoMatchType.ADDR_LINE2, DemoMatchType.ADDR_LINE3,
					DemoMatchType.LOCATION1, DemoMatchType.LOCATION2, DemoMatchType.LOCATION3, DemoMatchType.PINCODE),
			AuthTypeDTO::isDemo, "Address"),

	/** The pi pri. */
	PERSONAL_IDENTITY("personalIdentity",
			AuthType.setOf(DemoMatchType.NAME, DemoMatchType.DOB, DemoMatchType.DOBTYPE, DemoMatchType.AGE,
					DemoMatchType.EMAIL, DemoMatchType.PHONE, DemoMatchType.GENDER),
			AuthTypeDTO::isDemo, "Personal Identity"),

	FULL_ADDRESS("fullAddress", AuthType.setOf(DemoMatchType.ADDR), AuthTypeDTO::isDemo, "Full Address")

	/**  */
	// @formatter:on
	;

	private AuthTypeImpl authTypeImpl;

	/**
	 * Instantiates a new demo auth type.
	 *
	 * @param type                 the type
	 * @param associatedMatchTypes the associated match types
	 * @param langType             the lang type
	 * @param authTypePredicate    the auth type predicate
	 * @param displayName          the display name
	 */
	private DemoAuthType(String type, Set<MatchType> associatedMatchTypes,
			Predicate<? super AuthTypeDTO> authTypePredicate, String displayName) {
		authTypeImpl = new AuthTypeImpl(type, associatedMatchTypes, authTypePredicate, displayName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.AuthType#getMatchProperties(io
	 * .mosip.authentication.core.dto.indauth.AuthRequestDTO,
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher)
	 */
	@Override
	public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
			BioMatcherUtil bioMatcherUtil, String language) {
		HashMap<String, Object> valuemap = new HashMap<>();
		Optional<String> languageNameOpt = idInfoFetcher.getLanguageName(language);
		valuemap.put("language", languageNameOpt.orElse("english"));
		valuemap.put("env", idInfoFetcher.getEnvironment());
		valuemap.put("titlesFetcher", idInfoFetcher.getTitleFetcher());
		valuemap.put("langCode", language);
		return valuemap;
	}

	@Override
	public AuthType getAuthTypeImpl() {
		return authTypeImpl;
	}

}
