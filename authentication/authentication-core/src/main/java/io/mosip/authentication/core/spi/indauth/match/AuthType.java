package io.mosip.authentication.core.spi.indauth.match;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

/**
 * Base interface for the Auth type.
 */
public interface AuthType {

	public static final int DEFAULT_MATCHING_THRESHOLD = 100;

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	String getDisplayName();

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	String getType();

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
	 * Checks if is auth type enabled.
	 *
	 * @param authReq the auth req
	 * @param helper  the helper
	 * @return true, if is auth type enabled
	 */
	boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper);

	/**
	 * Gets the matching strategy.
	 *
	 * @param authReq             the auth req
	 * @param languageInfoFetcher the language info fetcher
	 * @return the matching strategy
	 */
	Optional<String> getMatchingStrategy(AuthRequestDTO authReq, String language);

	/**
	 * Gets the matching threshold.
	 *
	 * @param authReq             the auth req
	 * @param languageInfoFetcher the language info fetcher
	 * @param environment         the environment
	 * @return the matching threshold
	 */
	public default Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String language,
			Environment environment) {
		return Optional.of(DEFAULT_MATCHING_THRESHOLD);
	}

	/**
	 * Gets the associated match types.
	 *
	 * @return the associated match types
	 */
	Set<MatchType> getAssociatedMatchTypes();

	/**
	 * Checks if is auth type info available.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return true, if is auth type info available
	 */
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO);

	/**
	 * Gets the match properties.
	 *
	 * @param authRequestDTO      the auth request DTO
	 * @param languageInfoFetcher the language info fetcher
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

}
