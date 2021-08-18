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
	 * @param language the language
	 * @return the matching strategy
	 */
	default Optional<String> getMatchingStrategy(AuthRequestDTO authReq, String language) {
		return Optional.of(MatchingStrategyType.EXACT.getType());
	}

	/**
	 * Gets the matching threshold.
	 *
	 * @param authReq             the auth req
	 * @param language the language
	 * @param environment         the environment
	 * @param idInfoFetcher the id info fetcher
	 * @return the matching threshold
	 */
	public default Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String language,
			Environment environment, IdInfoFetcher idInfoFetcher) {
		return Optional.of(DEFAULT_MATCHING_THRESHOLD);
	}

	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO);

	/**
	 * Gets the match properties.
	 *
	 * @param authRequestDTO      the auth request DTO
	 * @param languageInfoFetcher the language info fetcher
	 * @param bioMatcherUtil the bio matcher util
	 * @param language the language
	 * @return the match properties
	 */
	public default Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO,
			IdInfoFetcher languageInfoFetcher, String language) {
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
	 * Returns the set of given match types.
	 *
	 * @param supportedMatchTypes the supported match types
	 * @return the sets the
	 */
	public static <T> Set<T> setOf(T... supportedMatchTypes) {
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
	
	public default String[] getTypes() {
		return new String[] {getType()};
	}

	/**
	 * Checks if is auth type info available.
	 *
	 * @param authReq the auth req
	 * @param idInfoFetcher the id info fetcher
	 * @return true, if is auth type info available
	 */
	public default boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return Optional.of(authReq).filter(getAuthTypeImpl().getAuthTypePredicate()).isPresent();
	}

	/**
	 * Gets the associated match types.
	 *
	 * @return the associated match types
	 */
	public default Set<MatchType> getAssociatedMatchTypes() {
		return Collections.unmodifiableSet(getAuthTypeImpl().getAssociatedMatchTypes());
	}

	/**
	 * Gets the auth type predicate.
	 *
	 * @return the auth type predicate
	 */
	public default Predicate<? super AuthRequestDTO> getAuthTypePredicate() {
		return getAuthTypeImpl().getAuthTypePredicate();
	}

	public default String getDisplayName(AuthRequestDTO authReq, IdInfoFetcher helper) {
		return getDisplayName();
	}
}
