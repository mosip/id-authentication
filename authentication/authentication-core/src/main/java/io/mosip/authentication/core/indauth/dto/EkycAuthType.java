package io.mosip.authentication.core.indauth.dto;

import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The enum for EkycAuthType
 * 
 * @author Prem Kumar
 *
 *
 *
 */

public enum EkycAuthType {

	/** For demo */
	DEMO("demo"),

	/** For bio */
	BIO("bio"),

	/** For Pin */
	PIN("pin"),

	/** For OTP */
	OTP("otp");

	/** The EkycAuthType type */
	private String type;

	/**
	 * Instantiates a new EkycAuthType.
	 *
	 * @param type the type
	 * @param type the authTypePredicate
	 */
	private EkycAuthType(String type) {
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
	 * This method returns the EkycAuthType based on the type.
	 *
	 * @param type the type
	 * @return EkycAuthType
	 */
	public static Optional<EkycAuthType> getEkycAuthType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}


}
