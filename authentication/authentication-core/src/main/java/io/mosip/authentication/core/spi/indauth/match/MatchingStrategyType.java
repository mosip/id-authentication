package io.mosip.authentication.core.spi.indauth.match;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Arun Bose
 * The Enum MatchStrategyType.
 */
public enum MatchingStrategyType {

	/** The exact. */
	EXACT("E"),
	/** The partial. */
	PARTIAL("P"),
	/** The phonetics. */
	PHONETICS("PH");

	/** The Constant default_Matching_Strategy. */
	public static final MatchingStrategyType DEFAULT_MATCHING_STRATEGY = MatchingStrategyType.EXACT;

	/** The type. */
	private String type;

	/**
	 * Instantiates a new match strategy type.
	 *
	 * @param type the type
	 */
	private MatchingStrategyType(String type) {
		this.type = type;
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
	 * Gets the match strategy type.
	 *
	 * @param type the type
	 * @return the match strategy type
	 */
	public static Optional<MatchingStrategyType> getMatchStrategyType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}
}
