package io.mosip.authentication.core.spi.idauth.demo;

import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Generic enum class for demographic Authentication.
 *
 * @author Rakesh Roshan
 */
public enum MatchingStrategyType {

	EXACT("E"), 
	PARTIAL("P"),
	PHONETICS("PH");
	// DEFAULT_MATCHING_STRATEGY_TYPE;

	/** The type. */
	private String type;

	/**
	 * Instantiates a new pin type.
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
	@JsonValue
	@XmlValue
	public String getType() {
		return type;
	}

	/**
	 * This method returns the MatchingStrategyType based on the type.
	 *
	 * @param type the type
	 * @return PinType
	 */
	public static Optional<MatchingStrategyType> getMatchingStrategyType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}
}
