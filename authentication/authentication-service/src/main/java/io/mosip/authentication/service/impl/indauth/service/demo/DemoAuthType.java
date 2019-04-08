package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
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
					DemoMatchType.LOCATION1, DemoMatchType.LOCATION2, DemoMatchType.LOCATION3,
					DemoMatchType.PINCODE), AuthTypeDTO::isDemo, "Address"),

	/** The pi pri. */
	PERSONAL_IDENTITY("personalIdentity",
			AuthType.setOf(DemoMatchType.NAME, DemoMatchType.DOB, DemoMatchType.DOBTYPE, DemoMatchType.AGE,
					DemoMatchType.EMAIL, DemoMatchType.PHONE, DemoMatchType.GENDER), AuthTypeDTO::isDemo, "Personal Identity"),
	
	FULL_ADDRESS("fullAddress", AuthType.setOf(DemoMatchType.ADDR), AuthTypeDTO::isDemo,
			"Full Address")
	
	

	/**  */
	// @formatter:on
	;

	/** The type. */
	private String type;

	/** The associated match types. */
	private Set<MatchType> associatedMatchTypes;

	/** The auth type predicate. */
	private Predicate<? super AuthTypeDTO> authTypePredicate;

	/** The display name. */
	private String displayName;

	/**
	 * Instantiates a new demo auth type.
	 *
	 * @param type the type
	 * @param associatedMatchTypes the associated match types
	 * @param langType the lang type
	 * @param authTypePredicate the auth type predicate
	 * @param displayName the display name
	 */
	private DemoAuthType(String type, Set<MatchType> associatedMatchTypes,
			Predicate<? super AuthTypeDTO> authTypePredicate, String displayName) {
		this.type = type;
		this.authTypePredicate = authTypePredicate;
		this.displayName = displayName;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.impl.indauth.builder.AuthType#getDisplayName(
	 * )
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * isAuthTypeEnabled(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return Optional.of(authReq).map(AuthRequestDTO::getRequestedAuth).filter(authTypePredicate).isPresent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * getMatchingThreshold(io.mosip.authentication.core.dto.indauth.AuthRequestDTO,
	 * java.util.function.Function)
	 */
	@Override
	public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq,
			String language, Environment environment) {
		return Optional.of(AuthType.DEFAULT_MATCHING_THRESHOLD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * getAssociatedMatchTypes()
	 */
	@Override
	public Set<MatchType> getAssociatedMatchTypes() {
		return Collections.unmodifiableSet(associatedMatchTypes);
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getMatchProperties(io.mosip.authentication.core.dto.indauth.AuthRequestDTO, io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher)
	 */
	@Override
	public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher, String language) {
		HashMap<String, Object> valuemap = new HashMap<>();
		Optional<String> languageNameOpt = idInfoFetcher.getLanguageName(language);
		valuemap.put("language", languageNameOpt.orElse("english"));
		valuemap.put("env", idInfoFetcher.getEnvironment());
		valuemap.put("titlesFetcher", idInfoFetcher.getTitleFetcher());
		valuemap.put("langCode", language);
		return valuemap;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#isAuthTypeInfoAvailable(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	@Override
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		return false;
	}
	

}
