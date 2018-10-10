package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Arun Bose
 * The Enum MatchStrategyType.
 */
public enum MatchStrategyType {

	/** The exact. */
	EXACT("EXACT"), /** The partial. */
 PARTIAL("PARTIAL"), /** The phonetics. */
 PHONETICS("PHONETICS");

	/** The Constant default_Matching_Strategy. */
	public static final MatchStrategyType default_Matching_Strategy = MatchStrategyType.EXACT;

	/** The type. */
	private String type;

	/**
	 * Instantiates a new match strategy type.
	 *
	 * @param type the type
	 */
	private MatchStrategyType(String type) {
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
	public static Optional<MatchStrategyType> getMatchStrategyType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}
}
