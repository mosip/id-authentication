package io.mosip.authentication.core.spi.indauth.match;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.spi.bioauth.util.BioMatcherUtil;

/**
 * Auth type interface
 * 
 * @author Dinesh Karuppiah.T
 *
 */
public interface AuthType {

	public static final int DEFAULT_MATCHING_THRESHOLD = 100;

	/**
	 * Checks if is associated match type.
	 *
	 * @param matchType the match type
	 * @return true, if is associated match type
	 */
	public default boolean isAssociatedMatchType(MatchType matchType) {
		return getAssociatedMatchTypes().contains(matchType);
	}

	/**
	 * Gets the matching strategy.
	 *
	 * @param authReq             the auth req
	 * @param languageInfoFetcher the language info fetcher
	 * @return the matching strategy
	 */
	default Optional<String> getMatchingStrategy(AuthRequestDTO authReq, String language) {
		return Optional.of(MatchingStrategyType.EXACT.getType());
	}

	/**
	 * Gets the matching threshold.
	 *
	 * @param authReq             the auth req
	 * @param languageInfoFetcher the language info fetcher
	 * @param environment         the environment
	 * @return the matching threshold
	 */
	public default Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String language,
			Environment environment, IdInfoFetcher idInfoFetcher) {
		return Optional.of(DEFAULT_MATCHING_THRESHOLD);
	}

	public default boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		return false;
	}

	/**
	 * Gets the match properties.
	 *
	 * @param authRequestDTO      the auth request DTO
	 * @param languageInfoFetcher the language info fetcher
	 * @return the match properties
	 */
	public default Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO,
			IdInfoFetcher languageInfoFetcher, BioMatcherUtil bioMatcherUtil, String language) {
		return Collections.emptyMap();
	}

	/**
	 * Gets the auth type for match type.
	 *
	 * @param matchType the match type
	 * @param authTypes the auth types
	 * @return the auth type for match type
	 */
	public static Optional<AuthType> getAuthTypeForMatchType(MatchType matchType, AuthType[] authTypes) {
		return Stream.of(authTypes).filter(at -> at.isAssociatedMatchType(matchType)).findAny();
	}

	/**
	 * Returns the set of given match types
	 *
	 * @param supportedMatchTypes the supported match types
	 * @return the sets the
	 */
	public static Set<MatchType> setOf(MatchType... supportedMatchTypes) {
		return Stream.of(supportedMatchTypes).collect(Collectors.toSet());
	}

	/**
	 * Gets the auth type impl.
	 *
	 * @return the auth type impl
	 */
	public AuthType getAuthTypeImpl();

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public default String getDisplayName() {
		return getAuthTypeImpl().getDisplayName();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public default String getType() {
		return getAuthTypeImpl().getType();
	}

	/**
	 * Checks if is auth type info available.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return true, if is auth type info available
	 */
	public default boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return Optional.of(authReq).map(AuthRequestDTO::getRequestedAuth)
				.filter(getAuthTypeImpl().getAuthTypePredicate()).isPresent();
	}

	/**
	 * Gets the associated match types.
	 *
	 * @return the associated match types
	 */
	public default Set<MatchType> getAssociatedMatchTypes() {
		return Collections.unmodifiableSet(getAuthTypeImpl().getAssociatedMatchTypes());
	}

	public default Predicate<? super AuthTypeDTO> getAuthTypePredicate() {
		return getAuthTypeImpl().getAuthTypePredicate();
	}
}
