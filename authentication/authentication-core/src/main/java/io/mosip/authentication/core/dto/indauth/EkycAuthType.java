package io.mosip.authentication.core.dto.indauth;

import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * 
 * @author Prem Kumar
 *
 */

public enum EkycAuthType {
		
	/** For Fingerprints  */
	FINGERPRINTS("F"),
	
	/** For Iris */
	IRIS("I"),
	
	/** For Face */
	FACE("A"),
	
	/** For Pin */
	PIN("P"),
	/** For OTP */
	OTP("O");
	
	
	String type;
	
	/**
	 * Instantiates a new EkycAuthType.
	 *
	 * @param type the EkycAuthType
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
