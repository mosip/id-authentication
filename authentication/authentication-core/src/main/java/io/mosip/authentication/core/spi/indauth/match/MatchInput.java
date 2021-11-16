package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The Class MatchInput constructs the core match value of the  which has to be authorised.
 *
 * @author Arun Bose .
 */

/**
 * Instantiates a new match input.
 *
 * @param authType the auth type
 * @param matchType the match type
 * @param matchStrategyType the match strategy type
 * @param matchValue the match value
 * @param matchProperties the match properties
 * @param language the language
 */
@Getter 
@Setter 
@RequiredArgsConstructor 
@ToString 
@AllArgsConstructor
public class MatchInput {

	/** The match type. */
	private AuthType authType;
	
	/** The match type. */
	private String idName;

	/** The match type. */
	private MatchType matchType;

	/** The match strategy type. */
	private String matchStrategyType;

	/** The match value. */
	private Integer matchValue;

	/** The match properties. */
	private Map<String, Object> matchProperties;

	/** The language. */
	private String language;

	@Override
	public int hashCode() {
		return Objects.hash(authType, idName, language, matchType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchInput other = (MatchInput) obj;
		return Objects.equals(authType, other.authType) && Objects.equals(idName, other.idName)
				&& Objects.equals(language, other.language) && Objects.equals(matchType, other.matchType);
	}

}
