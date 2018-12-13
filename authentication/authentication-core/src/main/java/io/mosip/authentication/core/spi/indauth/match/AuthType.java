package io.mosip.authentication.core.spi.indauth.match;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

/**
 * The Enum AuthType.
 */
public interface AuthType {

	String getDisplayName();

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	String getType();

	LanguageType getLangType();

	/**
	 * 
	 *
	 * @param matchType
	 * @return
	 */
	boolean isAssociatedMatchType(MatchType matchType);

	boolean isAuthTypeEnabled(AuthRequestDTO authReq);

	Optional<String> getMatchingStrategy(AuthRequestDTO authReq, Function<LanguageType, String> languageInfoFetcher);

	Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, Function<LanguageType, String> languageInfoFetcher, Environment environment);

	Set<MatchType> getAssociatedMatchTypes();
	
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO);

	public default Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO,
			Function<LanguageType, String> languageInfoFetcher) {
		return Collections.emptyMap();
	}

	/**
	 * 
	 *
	 * @param matchType
	 * @return
	 */
	public static Optional<AuthType> getAuthTypeForMatchType(MatchType matchType, AuthType[] authTypes) {
		return Stream.of(authTypes).filter(at -> at.isAssociatedMatchType(matchType)).findAny();
	}
	
}
