package io.mosip.authentication.core.dto.indauth;

import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The class PinType holds all the PinType(s).
 *
 * @author Arun Bose
 * @author Rakesh Roshan
 */
public enum PinType {

	/** The otp. */
	OTP("OTP"),
	
	/** The pin. */
	PIN("SPIN");

	/** The type. */
	private String type;

	/**
	 * Instantiates a new pin type.
	 *
	 * @param type the type
	 */
	private PinType(String type) {
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

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getType();
	}

	/**
	 * This method returns the PinType based on the type.
	 *
	 * @param type the type
	 * @return PinType
	 */
	public static Optional<PinType> getPINType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}
}
