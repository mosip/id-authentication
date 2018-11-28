package io.mosip.authentication.core.dto.indauth;

import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * BioType enum for Biometric type.
 *
 * @author Rakesh Roshan
 */
public enum BioType {

	
	FGRMIN("fgrMin"),
	FGRIMG("fgrImg"),
	IRISIMG("irisImg"),
	FACEIMG("faceImg");

	/** The type. */
	String type;

	/**
	 * Instantiates a new pin type.
	 *
	 * @param type the bio-type
	 */
	private BioType(String type) {
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
	 * This method returns the PinType based on the type.
	 *
	 * @param type the type
	 * @return BioType
	 */
	public static Optional<BioType> getBioType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}
}
