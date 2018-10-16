package io.mosip.authentication.service.impl.indauth.builder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchType;

/**
 * The Enum PartialMatchType.
 */
public enum AuthType {
	
	/** The pi pri. */
	PI_PRI("piPri", setOf(DemoMatchType.NAME_PRI)), 
	
	/** The pi sec. */
	PI_SEC("piSec", setOf(DemoMatchType.NAME_SEC)), 
	
	/** The fad pri. */
	FAD_PRI("fadPri", setOf(DemoMatchType.ADDR_PRI)), 
	
	/** The fad sec. */
	FAD_SEC("fadSec", setOf(DemoMatchType.ADDR_PRI)), 
	
	/** The bio. */
	BIO("bio", Collections.emptySet())
	
	;
	
	/** The type. */
	private String type;
	private Set<MatchType> associatedMatchTypes;

	/**
	 * Instantiates a new match info type.
	 *
	 * @param type the type
	 */
	private AuthType(String type, Set<MatchType> associatedMatchTypes) {
		this.type = type;
		this.associatedMatchTypes = associatedMatchTypes;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the of.
	 *
	 * @param matchingStrategies the matching strategies
	 * @return the sets the
	 */
	public static Set<MatchType> setOf(MatchType... supportedMatchTypes) {
		return Stream.of(supportedMatchTypes).collect(Collectors.toSet());
	}
	
	public boolean isAssociatedMatchType(MatchType matchType) {
		return associatedMatchTypes.contains(matchType);
	}
	
	public static Optional<AuthType> getAuthTypeForMatchType(MatchType matchType) {
		return Stream.of(values())
				.filter(at -> at.isAssociatedMatchType(matchType))
				.findAny();
	}
}
